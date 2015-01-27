# -*- coding: utf-8 -*-
from scrapy.spider import Spider
from scrapy.selector import Selector
from specscrawl.items import SpecscrawlItem
import MySQLdb
import re
import string
from scrapy.utils.project import get_project_settings 

class AlternateSpider(Spider):

	settings = get_project_settings() 
	conn = MySQLdb.connect(user= settings['MYSQL_USER'], passwd= settings['MYSQL_PASS'], db= settings['MYSQL_DB'], host= settings['MYSQL_HOST'], charset='utf8', use_unicode=True)
	cursor = conn.cursor()
	try:
		cursor.execute("""SELECT url FROM url_ean WHERE ean = "" AND url LIKE '%alternate%' """)
		data = cursor.fetchall()
		start_urls = []
		for row in data:
			start_urls.append(row[0])
		
	except MySQLdb.Error, e:
		print "Error %d: %s" % (e.args[0], e.args[1])
	
	name = "alternate"
	allowed_domains = ["alternate.nl"]

	def parse(self, response):
		print "next item"
		item = SpecscrawlItem()
		datalist = response.xpath('//*[@class="techData"]')
		
		ean = response.xpath('//*[@id="buyProduct"]/script[1]/text()').extract()
		ean = (''.join(ean)).split("upcean")[1]
		ean = re.findall(r'\d+', ''.join(ean))[0]
		if(len(ean) < 5):
			ean = ""
		
		breadcrumbs = datalist.xpath('//*[@id="contentWrapper"]/div[1]').extract()
		component = datalist.xpath('//*[@id="contentWrapper"]/div[1]/span[2]/a/span/text()').extract()[0]
		if "Voedingen" in "".join(breadcrumbs):
			component = datalist.xpath('//*[@id="contentWrapper"]/div[1]/span[3]/a/span/text()').extract()[0]
		naam = datalist.xpath('//*[@id="buyProduct"]/div[1]/h1/span[2]/text()').extract()[0]
		item['specs'] ={}	
		item['specs'] = {"component": component,
							"merk":datalist.xpath('//*[@id="buyProduct"]/div[1]/h1/span[1]/text()').extract()[0],
							"naam":naam,
							"url": response.url,
							"ean":ean
							}
		
		tempkeys = datalist.xpath('//*[@class="techDataCol1"]/text()').extract()
		tempvalue = datalist.xpath('//*[@class="techDataCol2"]')
		if(tempkeys):
			
			for i in range(len(tempkeys)):
				tempkeys[i] = re.sub("[.]",",",tempkeys[i])
				value = tempvalue[i].extract()
				keys = re.findall('techDataSubCol techDataSubColDescription">(.*?)</td><td class="techDataSubCol techDataSubColValue"><table cellpadding="0" cellspacing="0"><tr><td class="techDataSubCol techDataSubColValue">' ,value)
				values = re.findall('<td class="techDataSubCol techDataSubColValue"><table cellpadding="0" cellspacing="0"><tr><td class="techDataSubCol techDataSubColValue">(.*?)</td></tr></table>',value)	
				for key in keys:
					key = re.sub("[.]",",",key)
					
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
			cursor.execute("""UPDATE url_ean SET ean = %s WHERE url = %s""", (ean, response.url))
			self.conn.commit()
			
		except MySQLdb.Error, e:
			print "Error %d: %s hoi" % (e.args[0], e.args[1])
		cursor.close()
		return item
