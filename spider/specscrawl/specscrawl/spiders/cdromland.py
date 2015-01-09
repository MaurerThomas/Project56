# -*- coding: utf-8 -*-
from scrapy.spider import Spider
from scrapy.selector import Selector
from specscrawl.items import SpecscrawlItem
import MySQLdb
import re
import string

class cdromlandSpider(Spider):

	conn = MySQLdb.connect(user='pcbuilder', passwd='project', db='pcbuilder', host='127.0.0.1', charset='utf8', use_unicode=True)
	cursor = conn.cursor()
	try:
		cursor.execute("""SELECT url FROM url_crawled WHERE ean = null AND url LIKE '%cdromland%' """)
		data = cursor.fetchall()
		start_urls = []
		for row in data:
			start_urls.append(row[0])
		
	except MySQLdb.Error, e:
		print "Error %d: %s" % (e.args[0], e.args[1])
	
	name = "cdromland"
	allowed_domains = ["cdromland.nl"]

	def parse(self, response):
		
		ean = response.xpath('//*[@id="sitemain_content_cell"]/div/div[7]/div/div[2]/div[1]/div/div/div/div[18]/div[2]').extract()
					
		cursor = self.conn.cursor()
		try:
			cursor.execute("""UPDATE url_crawled SET ean = %s WHERE url = %s""", (ean, response.url))
			self.conn.commit()
			
		except MySQLdb.Error, e:
			print "Error %d: %s hoi" % (e.args[0], e.args[1])
		cursor.close()
		return item
