
# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# http://doc.scrapy.org/en/latest/topics/items.html

import scrapy

class AlternateItem(scrapy.Item):
	url = scrapy.Field()
	euro = scrapy.Field()
	cent = scrapy.Field()