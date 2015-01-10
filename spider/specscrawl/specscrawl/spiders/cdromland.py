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
		cursor.execute("""SELECT url FROM url_ean WHERE ean = "" AND url LIKE '%cdromland%'; """)
		data = cursor.fetchall()
		start_urls = []
		for row in data:
			start_urls.append(row[0])
		
	except MySQLdb.Error, e:
		print "Error %d: %s" % (e.args[0], e.args[1])
	
	name = "cdromland"
	allowed_domains = ["cdromland.nl"]

	def parse(self, response):
		
		ean = response.xpath('//*[@class = "product_cell_specs_content"]').extract()
		ean = re.findall('<div class="product_speclist_1"><h5>EAN</h5></div><div class="product_speclist_2">(.*)</div></div>', ''.join(ean))
		
		item = SpecscrawlItem()
		cursor = self.conn.cursor()
		try:
			cursor.execute("""UPDATE url_ean SET ean = %s WHERE url = %s""", (ean[0], response.url))
			self.conn.commit()
			
		except MySQLdb.Error, e:
			print "Error %d: %s hoi" % (e.args[0], e.args[1])
		cursor.close()
		return item
