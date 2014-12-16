# -*- coding: utf-8 -*-
import scrapy
from scrapy.spider import Spider
from scrapy.selector import Selector
from alternate.items import AlternateItem
import re


class SpinSpider(scrapy.Spider):
    name = "cdromland"
    allowed_domains = ["cdromland.nl"]
    start_urls = (
        'https://www.cdromland.nl/nav/948/PC-Voedingen',
    )

    def parse(self, response):
		products = response.xpath('//*[@class = "productlist_row"]')
		items = []
		for product in products:
			item = AlternateItem()
			temp = product.xpath('a/span/span/h2/*[starts-with(@class,"name")]/span/text()').extract()
			item['url'] = "https://www.cdromland.nl" + str(product.xpath('/*[@class = "link_titlesmall"]/@href').extract())
			tempeuro = product.xpath('/*[@class = "productlist_cell_specs_footer_price"]/text()').extract()
			item['euro'] = re.findall(r'\d+', ''.join(tempeuro))
			print item['euro']
			print item['cent']
			print "=========================="
			
			items.append(item)			
		return items