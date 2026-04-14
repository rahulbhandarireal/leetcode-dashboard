from pathlib import Path
import textwrap


ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "docs" / "issue-fix-report.md"
OUTPUT = ROOT / "issue-fix-report.pdf"


PAGE_WIDTH = 595
PAGE_HEIGHT = 842
LEFT = 50
RIGHT = 50
TOP = 60
BOTTOM = 50
FONT_SIZE = 11
LEADING = 15
USABLE_WIDTH = PAGE_WIDTH - LEFT - RIGHT
CHARS_PER_LINE = 92


def escape_pdf_text(text: str) -> str:
    return text.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)")


def wrap_paragraph(paragraph: str):
    stripped = paragraph.rstrip()
    if not stripped:
        return [""]
    if stripped.startswith("#"):
        content = stripped.lstrip("#").strip()
        return [content.upper()]
    if stripped.startswith("- "):
        return textwrap.wrap(stripped, width=CHARS_PER_LINE, subsequent_indent="  ") or [stripped]
    return textwrap.wrap(stripped, width=CHARS_PER_LINE) or [stripped]


def build_lines(text: str):
    lines = []
    for raw in text.splitlines():
        wrapped = wrap_paragraph(raw)
        lines.extend(wrapped)
        if raw.strip() == "":
            lines.append("")
    return lines


def paginate(lines):
    pages = []
    current = []
    y = PAGE_HEIGHT - TOP
    for line in lines:
        if y < BOTTOM:
            pages.append(current)
            current = []
            y = PAGE_HEIGHT - TOP
        current.append((line, y))
        y -= LEADING
    if current:
        pages.append(current)
    return pages


def page_stream(lines_with_y):
    parts = ["BT", f"/F1 {FONT_SIZE} Tf"]
    for line, y in lines_with_y:
        text = escape_pdf_text(line)
        parts.append(f"1 0 0 1 {LEFT} {y} Tm ({text}) Tj")
    parts.append("ET")
    return "\n".join(parts).encode("latin-1", errors="replace")


def build_pdf(pages):
    objects = []

    def add_object(data: bytes) -> int:
        objects.append(data)
        return len(objects)

    font_obj = add_object(b"<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>")

    page_obj_ids = []
    content_obj_ids = []
    for page in pages:
        stream = page_stream(page)
        content = (
            f"<< /Length {len(stream)} >>\nstream\n".encode("latin-1")
            + stream
            + b"\nendstream"
        )
        content_obj_ids.append(add_object(content))
        page_obj_ids.append(0)

    pages_placeholder = add_object(b"")

    for i, content_id in enumerate(content_obj_ids):
        page_data = (
            f"<< /Type /Page /Parent {pages_placeholder} 0 R /MediaBox [0 0 {PAGE_WIDTH} {PAGE_HEIGHT}] "
            f"/Resources << /Font << /F1 {font_obj} 0 R >> >> /Contents {content_id} 0 R >>"
        ).encode("latin-1")
        page_obj_ids[i] = add_object(page_data)

    kids = " ".join(f"{pid} 0 R" for pid in page_obj_ids)
    pages_data = f"<< /Type /Pages /Count {len(page_obj_ids)} /Kids [{kids}] >>".encode("latin-1")
    objects[pages_placeholder - 1] = pages_data

    catalog_obj = add_object(f"<< /Type /Catalog /Pages {pages_placeholder} 0 R >>".encode("latin-1"))

    output = bytearray(b"%PDF-1.4\n")
    offsets = [0]
    for idx, obj in enumerate(objects, start=1):
        offsets.append(len(output))
        output.extend(f"{idx} 0 obj\n".encode("latin-1"))
        output.extend(obj)
        output.extend(b"\nendobj\n")

    xref_start = len(output)
    output.extend(f"xref\n0 {len(objects) + 1}\n".encode("latin-1"))
    output.extend(b"0000000000 65535 f \n")
    for off in offsets[1:]:
        output.extend(f"{off:010d} 00000 n \n".encode("latin-1"))
    output.extend(
        (
            f"trailer\n<< /Size {len(objects) + 1} /Root {catalog_obj} 0 R >>\n"
            f"startxref\n{xref_start}\n%%EOF"
        ).encode("latin-1")
    )
    return output


def main():
    text = SOURCE.read_text(encoding="utf-8")
    lines = build_lines(text)
    pages = paginate(lines)
    pdf = build_pdf(pages)
    OUTPUT.write_bytes(pdf)
    print(OUTPUT)


if __name__ == "__main__":
    main()
