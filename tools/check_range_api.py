# 校验区间 target 的产物：所有 net/minecraft 成员引用在 26.2 里必须以同样描述符存在。一套 jar 通吃 26.1.2 与 26.2 时，字段或方法描述符一变就是 NoSuchFieldError / NoSuchMethodError。

import struct
import sys
import zipfile
from pathlib import Path


class ClassFile:
    def __init__(self, data):
        self.data = data
        self.pool = {}
        self.refs = set()
        self.super_name = None
        self.interfaces = []
        self.fields = set()
        self.methods = set()
        self.name = None
        self._parse()

    def _u2(self, i):
        return struct.unpack_from(">H", self.data, i)[0], i + 2

    def _u4(self, i):
        return struct.unpack_from(">I", self.data, i)[0], i + 4

    def _parse(self):
        if self.data[:4] != b"\xca\xfe\xba\xbe":
            raise ValueError("not a class file")
        i = 8
        count, i = self._u2(i)
        raw = {}
        n = 1
        while n < count:
            tag = self.data[i]
            i += 1
            if tag == 1:
                length, i = self._u2(i)
                raw[n] = self.data[i:i + length].decode("utf-8", "replace")
                i += length
            elif tag in (7, 8, 16, 19, 20):
                raw[n] = (tag, struct.unpack_from(">H", self.data, i)[0])
                i += 2
            elif tag == 15:
                raw[n] = (tag,)
                i += 3
            elif tag in (3, 4, 9, 10, 11, 12, 17, 18):
                raw[n] = (tag,) + struct.unpack_from(">HH", self.data, i)
                i += 4
            elif tag in (5, 6):
                raw[n] = (tag,)
                i += 8
                n += 1
            else:
                raise ValueError("unknown constant pool tag %d" % tag)
            n += 1
        self.pool = raw
        _access, i = self._u2(i)
        this_idx, i = self._u2(i)
        super_idx, i = self._u2(i)
        self.name = self.class_name(this_idx)
        if super_idx:
            self.super_name = self.class_name(super_idx)
        icount, i = self._u2(i)
        for _ in range(icount):
            idx, i = self._u2(i)
            self.interfaces.append(self.class_name(idx))
        for bucket in (self.fields, self.methods):
            total, i = self._u2(i)
            for _ in range(total):
                _flags, i = self._u2(i)
                name_idx, i = self._u2(i)
                desc_idx, i = self._u2(i)
                bucket.add((raw[name_idx], raw[desc_idx]))
                attrs, i = self._u2(i)
                for _ in range(attrs):
                    _an, i = self._u2(i)
                    alen, i = self._u4(i)
                    i += alen
        self._collect_refs(raw)

    def class_name(self, idx):
        entry = self.pool[idx]
        return self.pool[entry[1]]

    def _collect_refs(self, raw):
        for entry in raw.values():
            if not isinstance(entry, tuple) or len(entry) != 3:
                continue
            tag = entry[0]
            if tag == 9:
                nat = raw.get(entry[2])
                if isinstance(nat, tuple) and len(nat) == 3 and isinstance(raw.get(nat[1]), str):
                    self.refs.add((self.class_name(entry[1]), raw[nat[1]], raw[nat[2]], "field"))
            elif tag in (10, 11):
                nat = raw.get(entry[2])
                if isinstance(nat, tuple) and len(nat) == 3 and isinstance(raw.get(nat[1]), str):
                    self.refs.add((self.class_name(entry[1]), raw[nat[1]], raw[nat[2]], "method"))


class JarIndex:
    def __init__(self, path):
        self.zip = zipfile.ZipFile(path)
        self.names = set(self.zip.namelist())
        self.cache = {}

    def entry(self, internal_name):
        name = internal_name + ".class"
        if name not in self.names:
            return None
        if name not in self.cache:
            self.cache[name] = ClassFile(self.zip.read(name))
        return self.cache[name]

    def has_member(self, internal_name, member, descriptor, kind, depth=0):
        info = self.entry(internal_name)
        if info is None or depth > 12:
            return False
        bucket = info.fields if kind == "field" else info.methods
        if (member, descriptor) in bucket:
            return True
        parents = list(info.interfaces)
        if info.super_name and not info.super_name.startswith("java/"):
            parents.append(info.super_name)
        return any(self.has_member(p, member, descriptor, kind, depth + 1) for p in parents)

    def has_java_ancestor(self, internal_name, depth=0):
        info = self.entry(internal_name)
        if info is None or depth > 12:
            return False
        if info.super_name and info.super_name.startswith("java/"):
            return True
        parents = list(info.interfaces)
        if info.super_name:
            parents.append(info.super_name)
        return any(self.has_java_ancestor(p, depth + 1) for p in parents)


def collect_refs(classes_dir):
    refs = {}
    for path in Path(classes_dir).rglob("*.class"):
        info = ClassFile(path.read_bytes())
        for owner, member, desc, kind in info.refs:
            if not owner.startswith("net/minecraft/"):
                continue
            refs.setdefault((owner, member, desc, kind), set()).add(path.name)
    return refs


def main():
    if len(sys.argv) < 4:
        print("用法: check_range_api.py <本模组 classes 目录> <26.2 合并 jar> <豁免清单>")
        return 2
    classes_dir, target_jar, allow_file = sys.argv[1], sys.argv[2], sys.argv[3]
    allowed = set()
    if Path(allow_file).exists():
        for line in Path(allow_file).read_text(encoding="utf-8").splitlines():
            line = line.strip()
            if line and not line.startswith("#"):
                allowed.add(line)
    target = JarIndex(target_jar)
    refs = collect_refs(classes_dir)
    missing = []
    unknown = []
    for (owner, member, desc, kind), files in sorted(refs.items()):
        key = "%s#%s" % (owner.replace("/", "."), member)
        if key in allowed:
            continue
        if target.has_member(owner, member, desc, kind):
            continue
        entry = ("%s %s" % (key, desc), sorted(files)[0])
        if target.has_java_ancestor(owner):
            unknown.append(entry)
        else:
            missing.append(entry)
    print("检查引用 %d 个，缺失 %d 个，无法判定 %d 个" % (len(refs), len(missing), len(unknown)))
    for key, where in missing:
        print("  缺失 %-74s <- %s" % (key, where))
    for key, where in unknown:
        print("  跳过 %-74s (JDK 继承) <- %s" % (key, where))
    return 1 if missing else 0


if __name__ == "__main__":
    sys.exit(main())
