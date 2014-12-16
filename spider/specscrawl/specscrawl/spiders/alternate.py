# -*- coding: utf-8 -*-
from scrapy.spider import Spider
from scrapy.selector import Selector
from specscrawl.items import SpecscrawlItem
import MySQLdb
import re

class AlternateSpider(Spider):

	conn = MySQLdb.connect(user='pcbuilder', passwd='project', db='pcbuilder', host='127.0.0.1', charset='utf8', use_unicode=True)
	cursor = conn.cursor()
	try:
		cursor.execute("""SELECT DISTINCT url FROM prijs_verloop WHERE crawled = 0""")
		data = cursor.fetchall()
		start_urls = []
		for row in data:
			start_urls.append(row[0])
		
	except MySQLdb.Error, e:
		print "Error %d: %s" % (e.args[0], e.args[1])
	
	name = "alternate"
	allowed_domains = ["alternate.nl"]
	

	def parse(self, response):
		item = SpecscrawlItem()
		datalist = response.xpath('//*[@class="techData"]')
		print datalist
		title = datalist.xpath('//*[starts-with(@class,"breadCrumbs")]/span/a/span/text()').extract()
		title = title[1]
		name = datalist.xpath('//*[starts-with(@class,"productNameContainer")]/*[starts-with(@itemprop,"name")]/text()').extract()
		brand = datalist.xpath('//*[starts-with(@class,"productNameContainer")]/*[starts-with(@itemprop,"brand")]/text()').extract()
		item['specs'] ={}
		item['specs'] = {"component":datalist.xpath('//*[@id="contentWrapper"]/div[1]/span[2]/a/span/text()').extract()[0],
							"merk":datalist.xpath('//*[@id="buyProduct"]/div[2]/h1/span[1]/text()').extract()[0],
							"naam":datalist.xpath('//*[@id="buyProduct"]/div[2]/meta[2]/@content').extract()[0],
							"url": response.url}
		tempkeys = datalist.xpath('//*[@class="techDataCol1"]/text()').extract()
		tempvalue = datalist.xpath('//*[@class="techDataCol2"]')
		if(tempkeys):
			for i in range(len(tempkeys)):
				value = tempvalue[i].extract()
				keys = re.findall('techDataSubCol techDataSubColDescription">(.*?)</td><td class="techDataSubCol techDataSubColValue"><table cellpadding="0" cellspacing="0"><tr><td class="techDataSubCol techDataSubColValue">' ,value)
				values = re.findall('<td class="techDataSubCol techDataSubColValue"><table cellpadding="0" cellspacing="0"><tr><td class="techDataSubCol techDataSubColValue">(.*?)</td></tr></table>',value)	
				for key in keys:
					key = re.sub(".",",",key)
				result = "null"
				if len(keys) > 0:
					result = zip(keys, values)
				if result == "null":
					result = re.findall('<td class="techDataSubCol techDataSubColValue">(.*)</td></tr></table>', value)
					
				elif isinstance(result, dict):
					for k, v in result.items():
						result[k] = re.sub('<[^>]+>', "", v)
				elif isinstance(result, list):
					for ri in result:
						result[result.index(ri)] = re.sub('<[^>]+>', "", str(ri))
				
				if len(result) < 1:
					item['specs'].update({tempkeys[i]:result})
				else:
					item['specs'].update({tempkeys[i]:result[0]})
					
		cursor = self.conn.cursor()
		try:
			cursor.execute("""UPDATE prijs_verloop SET crawled = %s WHERE url = %s""", ('1', response.url))
			self.conn.commit()
			
		except MySQLdb.Error, e:
			print "Error %d: %s hoi" % (e.args[0], e.args[1])
		cursor.close()
		return item
