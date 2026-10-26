#!/usr/bin/env python3
"""Datapack sanity check for the shared resource tree.

Run before every build: python tools/validate_pack.py [--jar <vanilla merged jar>]
Exits 1 and prints every problem, so a broken pack never reaches a build.
"""

import argparse
import glob
import json
import os
import re
import sys
import zipfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RES = os.path.join(ROOT, "common", "src", "main", "resources")
ASSETS = os.path.join(RES, "assets")
DATA = os.path.join(RES, "data")
HOLDER_SET_FIELDS = ("supported_items", "exclusive_set", "primary_items")
TAG_REF = re.compile(r"^#([a-z0-9_.-]+):([a-z0-9_./-]+)$")


def load_json(path):
    with open(path, encoding="utf-8") as handle:
        return json.load(handle)


def vanilla_tags(jar):
    if not jar or not os.path.isfile(jar):
        return set()
    found = set()
    with zipfile.ZipFile(jar) as archive:
        for name in archive.namelist():
            match = re.match(r"data/([^/]+)/tags/(?:item|block|enchantment|damage_type|entity_type)/(.+)\.json$", name)
            if match:
                found.add(f"#{match.group(1)}:{match.group(2)}")
    return found


def local_tags():
    found = set()
    for path in glob.glob(os.path.join(DATA, "*", "tags", "**", "*.json"), recursive=True):
        rel = os.path.relpath(path, DATA).replace(os.sep, "/")
        namespace, rest = rel.split("/", 1)
        kind = rest.split("/", 1)[1][: -len(".json")]
        for prefix in ("item", "block", "enchantment", "damage_type", "entity_type"):
            if kind.startswith(prefix + "/"):
                found.add(f"#{namespace}:{kind[len(prefix) + 1:]}")
                break
    return found


def local_items():
    found = set()
    for path in glob.glob(os.path.join(ASSETS, "*", "items", "**", "*.json"), recursive=True):
        rel = os.path.relpath(path, os.path.join(ASSETS)).replace(os.sep, "/")
        namespace, rest = rel.split("/", 1)
        found.add(f"{namespace}:{rest.split('/', 1)[1][: -len('.json')]}")
    for path in glob.glob(os.path.join(DATA, "*", "enchantment", "**", "*.json"), recursive=True):
        rel = os.path.relpath(path, DATA).replace(os.sep, "/")
        namespace, rest = rel.split("/", 1)
        found.add(f"{namespace}:{rest.split('/', 1)[1][: -len('.json')]}")
    java = os.path.join(ROOT, "common", "src", "main", "java")
    for path in glob.glob(os.path.join(java, "**", "*.java"), recursive=True):
        with open(path, encoding="utf-8") as handle:
            text = handle.read()
        for literal in re.findall(r'"([a-z0-9_]+)"', text):
            found.add(f"practical_enchantments:{literal}")
    return found


def tag_registry(path):
    rel = os.path.relpath(path, DATA).replace(os.sep, "/")
    parts = rel.split("/")
    if len(parts) > 2 and parts[1] == "tags":
        return parts[2]
    return ""


def check_holder_set(where, value, problems, known_tags=None, no_jar=False):
    if isinstance(value, str):
        match = TAG_REF.match(value)
        if (match and known_tags is not None and f"#{match.group(1)}:{match.group(2)}" not in known_tags
                and not (match.group(1) == "minecraft" and no_jar)):
            problems.append(f"{where}: unknown tag {value}")
        return
    if isinstance(value, list):
        for entry in value:
            if not isinstance(entry, str):
                problems.append(f"{where}: list entry is not a string: {entry!r}")
            elif entry.startswith("#"):
                problems.append(f"{where}: tag {entry} inside a list is not allowed, point at one tag instead")
        return
    problems.append(f"{where}: expected a string or a list, got {type(value).__name__}")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--jar", default="", help="merged vanilla jar, used to resolve #minecraft: tags")
    args = parser.parse_args()
    problems = []

    for path in glob.glob(os.path.join(RES, "**", "*.json"), recursive=True):
        try:
            load_json(path)
        except Exception as error:  # noqa: BLE001 - report every broken file
            problems.append(f"{os.path.relpath(path, ROOT)}: {error}")

    known_tags = local_tags() | vanilla_tags(args.jar)
    for path in glob.glob(os.path.join(DATA, "*", "enchantment", "**", "*.json"), recursive=True):
        rel = os.path.relpath(path, ROOT)
        data = load_json(path)
        for field in HOLDER_SET_FIELDS:
            if field in data:
                check_holder_set(f"{rel}: {field}", data[field], problems, known_tags, not args.jar)
        slots = data.get("slots")
        if not isinstance(slots, list):
            problems.append(f"{rel}: slots must be a list")
        for field in ("min_cost", "max_cost"):
            if field in data and not isinstance(data[field], dict):
                problems.append(f"{rel}: {field} must be an object")
        if not isinstance(data.get("effects"), dict):
            problems.append(f"{rel}: effects must be an object")

    known_items = local_items()
    for path in glob.glob(os.path.join(DATA, "**", "*.json"), recursive=True):
        rel = os.path.relpath(path, ROOT)
        try:
            data = load_json(path)
        except Exception:  # noqa: BLE001 - already reported above
            continue
        values = data.get("values") if isinstance(data, dict) else None
        if not isinstance(values, list) or tag_registry(path) not in ("item", "enchantment"):
            continue
        for entry in values:
            name = entry.get("id") if isinstance(entry, dict) else entry
            if not isinstance(name, str):
                continue
            match = TAG_REF.match(name)
            vanilla_unknown = match and not args.jar and match.group(1) == "minecraft"
            if match and not vanilla_unknown and f"#{match.group(1)}:{match.group(2)}" not in known_tags:
                problems.append(f"{rel}: unknown tag {name}")
            elif not match and name.startswith("practical_enchantments:") and name not in known_items:
                problems.append(f"{rel}: item {name} has no assets/*/items entry")

    if problems:
        print("\n".join(problems))
        print(f"\n{len(problems)} problem(s)")
        return 1
    print("pack ok")
    return 0


if __name__ == "__main__":
    sys.exit(main())
