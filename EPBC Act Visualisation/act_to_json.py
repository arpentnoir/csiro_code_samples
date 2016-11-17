'''
This is just an experiment in representing the structrue of the EPBC Act as XML for visualisation in D3... but I
got bored of that idea.
'''
import re
from xml.etree.ElementTree import Element, SubElement, Comment, tostring
from xmljson import parker
from xml.etree.ElementTree import fromstring
from json import dumps


chapter_pattern = re.compile("^CHAPTER")
part_pattern = re.compile("^PART")
division_pattern = re.compile("^Division")
subdivision_pattern = re.compile("^Subdivision")
section_pattern = re.compile("^[1-9]")

top = Element('legislation')

with open('table_of_provisions.txt') as input:
    content = input.readlines()

for line in content:
    if chapter_pattern.match(line):
        child = SubElement(top, 'children')
        chapter_name = SubElement(child, 'name')
        chapter_name.text = line.strip()
        current_chapter = child
        current_parent = current_chapter
    if part_pattern.match(line):
        child = SubElement(current_chapter, 'children')
        part_title = SubElement(child, 'name')
        part_title.text = line.strip()
        current_part = child
        current_parent = current_part
    if division_pattern.match(line):
        child = SubElement(current_part, 'children')
        division_title = SubElement(child, 'name')
        division_title.text = line.strip()
        current_division = child
        current_parent = current_division
    if subdivision_pattern.match(line):
        child = SubElement(current_division, 'children')
        subdivision_title = SubElement(child, 'name')
        subdivision_title.text = line.strip()
        current_subdivision = child
        current_parent = current_subdivision
    if section_pattern.match(line):
        child = SubElement(current_parent, 'children')
        section_title = SubElement(child, 'name')
        section_title.text = line.strip()

json_data = dumps(parker.data(fromstring(tostring(top))), indent=2)
print(json_data)

