'''
This script scans through a directory containing plain text copies of consolidated acts, and converts them to simple
XML and JSON files. The JSON structure just represents the act title and file name as seperate elements, and the
full text of the act as a single element.
'''

import os
import re
from xml.etree.ElementTree import Element, SubElement, Comment, tostring
from xmljson import parker
from xml.etree.ElementTree import fromstring
from json import dumps
from xml.dom import minidom

actname_pattern = re.compile('pic')


def compileXML(id, title, file_contents):
    top = Element('legislation')

    id_element = SubElement(top, 'id')
    id_element.text = id

    title_element = SubElement(top, 'title')
    title_element.text = title.strip()

    contents_element = SubElement(top, 'contents')
    contents_element.text = file_contents

    return top

for filename in os.listdir('texts'):
    if filename.endswith(".txt"):
        file = open('texts/' + filename)
        id = filename[:-4]
        title = file.readline()
        while True:
            if actname_pattern.search(title) or len(title.strip()) == 0:
                title = file.readline()
            else:
                break

        print('id = ' + id + ' title = ' + title)
        outputXML = open('xml/' + id + '.xml', 'w')
        xml_data = compileXML(id, title, file.read())
        pretty_xml_data = minidom.parseString(tostring(xml_data)).toprettyxml(indent="\t")
        outputXML.write(pretty_xml_data)
        outputXML.close()

        outputJSON = open('json/' + id + '.json', 'w')
        json_data = dumps(parker.data(fromstring(pretty_xml_data)), indent=2)
        outputJSON.write(json_data)
        outputJSON.close()

