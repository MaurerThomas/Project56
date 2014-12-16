# -*- coding: utf-8 -*-
import scrapy


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
			item['url'] = "https://www.cdromland.nl" + product.xpath('//*[@class = "link_titlesmall"]/@href').extract()
			tempeuro = product.xpath('//*[@id="art952263"]/div[1]/div/div[3]/div/div[3]/text()').extract()
			item['euro'] = re.findall(r'\d+', ''.join(tempeuro)[0]
			item['cent']	= re.findall(r'\d+', ''.join(tempeuro)[1]
			print item['euro']
			items.append(item)			
		return items