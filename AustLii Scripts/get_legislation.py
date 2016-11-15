'''
This script is used to download text files for all of the consolidated acts present on the AustLii Website.
Expects a folder called 'legislation' to exist in the same directory as the script. 
'''


from bs4 import BeautifulSoup
import urllib.request
import re
import os

table_of_contents_link_pattern = re.compile('^toc') # Links to lists beginning with a certain chafacter are of the form 'toc-A.html'
act_name_pattern = re.compile('\d{4}$') # all links to acts end in a year
base_url = 'http://www.austlii.edu.au/au/legis/cth/consol_act/'
base_download_url = 'http://www.austlii.edu.au/cgi-bin/download.cgi/cgi-bin/download.cgi/download/au/legis/cth/consol_act/'

def getbeginningwithlist(soup):
    beginningwith = []
    for link in soup.find_all('a'):
        if table_of_contents_link_pattern.match(link.get('href')):
            str = base_url + link.get('href')
            beginningwith.append(str)
    return beginningwith

def getactlist(soup):
    acts = []
    for link in soup.find_all('a'):
        if act_name_pattern.search(str(link.string)):
            string = link.get('href')
            acts.append(string)
    return acts

def getsoup(url):
    response = urllib.request.urlopen(url)
    html = response.read()
    return BeautifulSoup(html, 'html.parser')

def getactsforyear(url):
    soup = getsoup(url)
    return getactlist(soup)



list = getbeginningwithlist(getsoup(base_url + 'index.html'))


for link in list:
    act_list = getactlist(getsoup(link))
    for act in act_list:
        filename = 'legislation/' + act + '.txt'
        if os.path.isfile(filename):
            print("File already exists, skipping...")
        else:
            print("Downloading " + base_download_url + act + '.txt')
            urllib.request.urlretrieve(base_download_url + act + '.txt', filename)
            print('Done')

