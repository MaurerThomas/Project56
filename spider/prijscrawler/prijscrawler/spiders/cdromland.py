# -*- coding: utf-8 -*-
import scrapy
from scrapy.spider import Spider
from scrapy.selector import Selector
from prijscrawler.items import AlternateItem
import re


class CdromlandSpider(scrapy.Spider):
    name = "cdromland"
    allowed_domains = ["cdromland.nl"]
    start_urls = (
	'https://www.cdromland.nl/nav/868/Midi-Towers',
	'https://www.cdromland.nl/nav/869/Big-Towers',
	'https://www.cdromland.nl/nav/867/Micro-ATX',
	'https://www.cdromland.nl/nav/872/Mini-ITX',
	'https://www.cdromland.nl/nav/870/HTPC-Behuizingen',
	'https://www.cdromland.nl/nav/892/Intel-Socket-1150',
	'https://www.cdromland.nl/nav/889/AMD-Socket-AM3',
	'https://www.cdromland.nl/nav/891/Intel-Socket-2011',
	'https://www.cdromland.nl/nav/888/AMD-Socket-FM2',
	'https://www.cdromland.nl/nav/890/Intel-Socket-1155',
	'https://www.cdromland.nl/nav/883/Moederborden-CPU-Onboard',
	'https://www.cdromland.nl/nav/1307/Intel-Socket-2011-3',
	'https://www.cdromland.nl/nav/1307/Intel-Socket-2011-3',
	'https://www.cdromland.nl/nav/884/Intel-Socket-775',
	'https://www.cdromland.nl/nav/1262/AMD-Socket-AM1',
	'https://www.cdromland.nl/nav/1311/Intel-Socket-478',
	'https://www.cdromland.nl/nav/898/Intel-Socket-1150',
	'https://www.cdromland.nl/nav/895/Intel-Socket-1155',
	'https://www.cdromland.nl/nav/894/AMD-Socket-AM3',
	'https://www.cdromland.nl/nav/897/Intel-Socket-2011',
	'https://www.cdromland.nl/nav/896/AMD-Socket-FM2',
	'https://www.cdromland.nl/nav/1308/Intel-Socket-2011-3',
	'https://www.cdromland.nl/nav/1263/AMD-Socket-AM1',
	'https://www.cdromland.nl/nav/903/Hard-disks-3-5-S-ATA',
	'https://www.cdromland.nl/nav/1176/Solid-State-Drives',
	'https://www.cdromland.nl/nav/901/Hard-disks-2-5-S-ATA',
	'https://www.cdromland.nl/nav/942/PCI-Express-Nvidia',
	'https://www.cdromland.nl/nav/941/PCI-Express-AMD',
	'https://www.cdromland.nl/nav/940/AGP',
	'https://www.cdromland.nl/nav/881/DDR3',
	'https://www.cdromland.nl/nav/879/SO-DIMM',
	'https://www.cdromland.nl/nav/880/DDR2',
	'https://www.cdromland.nl/nav/876/DDR',
	'https://www.cdromland.nl/nav/1304/DDR4',
	'https://www.cdromland.nl/nav/955/Solid-State-Drives',
	'https://www.cdromland.nl/nav/911/CPU-cooler',
	'https://www.cdromland.nl/nav/929/Complete-sets',
	'https://www.cdromland.nl/nav/948/PC-Voedingen',
    )

    def parse(self, response):
		items = []
		urls = response.xpath('//*[@class = "link_titlesmall"]/@href').extract()
		euros = response.xpath('//*[@class = "productlist_cell_specs_footer_price"]/text()').extract()
		euros = [ v for v in euros if hasNumbers(v)]
		
		for index,url in enumerate(urls):
			item = AlternateItem()
			item['url'] = 'https://www.cdromland.nl' + url
			price = re.findall(r'\d+', ''.join(euros[index]))
			item['euro'] = price[0] 
			try:
				item['cent']	= price[1]
			except:
				item['cent'] = '0'
			items.append(item)			
		return items
	
def hasNumbers(inputString):
    return any(char.isdigit() for char in inputString)
		
		
