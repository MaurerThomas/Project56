# -*- coding: utf-8 -*-
from scrapy.spider import Spider
from scrapy.selector import Selector
from specscrawl.items import SpecscrawlItem
import re

class AlternateSpider(Spider):
	name = "alternate"
	allowed_domains = ["alternate.nl"]
	start_urls = (
		'https://www.alternate.nl/Aerocool/Dead-Silence-Black-White-Edition-behuizing/html/product/1096426?tk=7&lk=9309',
	)

	def parse(self, response):
		item = SpecscrawlItem()
		datalist = response.xpath('//*[starts-with(@class,"techData")]')
		title = datalist.xpath('//*[starts-with(@class,"breadCrumbs")]/span/a/span/text()').extract()
		title = title[1]
		name = datalist.xpath('//*[starts-with(@class,"productNameContainer")]/*[starts-with(@itemprop,"name")]/text()').extract()
		brand = datalist.xpath('//*[starts-with(@class,"productNameContainer")]/*[starts-with(@itemprop,"brand")]/text()').extract()
		item['specs'] ={}
		item['specs'] = {"name":name,"brand":brand}
		for data in datalist:
			tempkeys = data.xpath('//*[starts-with(@class,"techDataCol1")]/text()').extract()
			tempvalue = data.xpath('//*[starts-with(@class,"techDataCol2")]/text()').extract()
			print tempvalue
			if(tempkeys):
				for i in range(len(tempkeys)):
					try:
						item['specs'].update({tempkeys[i]:"hoi"})
					except IndexError:
						print tempkeys[i] + " does not exist"
		#item.append(itemarray)
		yield item
