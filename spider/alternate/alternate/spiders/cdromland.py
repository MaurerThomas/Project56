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
		products = response.xpath('//div/div/*[@class="productlist_row"]')
		items = []
		temp = products.xpath('a/span/span/h2/*[starts-with(@class,"name")]/span/text()').extract()
		urls = products.xpath('//*[@class = "link_titlesmall"]/@href').extract()
		euros = products.xpath('//*[@class = "productlist_cell_specs_footer_price"]/text()').extract()
		print euros
		for index,url in enumerate(urls):
			item = AlternateItem()
			item['url'] = 'https://www.cdromland.nl' + url
			price = re.findall(r'\d+', ''.join(euros[index]))
			item['euro'] = price[0]
			try:
				item['cent']	= price[1]
			except:
				item['cent'] = '0'
			print url
			print item['euro']
			print "=========================="
			items.append(item)			
		return items