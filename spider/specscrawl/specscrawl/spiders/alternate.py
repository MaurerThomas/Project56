# -*- coding: utf-8 -*-
from scrapy.spider import Spider
from scrapy.selector import Selector
from specscrawl.items import SpecscrawlItem
import re

class AlternateSpider(Spider):
#	def __init__(self):
#		self.conn = MySQLdb.connect(user='pcbuilder', passwd='project', db='pcbuilder', host='127.0.0.1', charset='utf8', use_unicode=True)
#		self.cursor = self.conn.cursor()
#		try:
#			start_urls = self.cursor.execute("""SELECT url FROM prijs_verloop WHERE crawled = 0""")
#			print start_urls
#			self.conn.commit()
#
#		except MySQLdb.Error, e:
#			print "Error %d: %s" % (e.args[0], e.args[1])
#	
	name = "alternate"
	allowed_domains = ["alternate.nl"]
	start_urls = (
		'https://www.alternate.nl/Aerocool/Dead-Silence-Black-White-Edition-behuizing/html/product/1096426?tk=7&lk=9309',
	)
	

	def parse(self, response):
		item = SpecscrawlItem()
		datalist = response.xpath('//*[@class="techData"]')
		print datalist
		title = datalist.xpath('//*[starts-with(@class,"breadCrumbs")]/span/a/span/text()').extract()
		title = title[1]
		name = datalist.xpath('//*[starts-with(@class,"productNameContainer")]/*[starts-with(@itemprop,"name")]/text()').extract()
		brand = datalist.xpath('//*[starts-with(@class,"productNameContainer")]/*[starts-with(@itemprop,"brand")]/text()').extract()
		item['specs'] ={}
		item['specs'] = {"name":name,"brand":brand}
		tempkeys = datalist.xpath('//*[@class="techDataCol1"]/text()').extract()
		print tempkeys
		tempvalue = datalist.xpath('//*[@class="techDataCol2"]')
		if(tempkeys):
			for i in range(len(tempkeys)):
				value = tempvalue[i].extract()
				keys = re.findall('techDataSubCol techDataSubColDescription">(.*?)</td><td' ,value)
				values = re.findall('<td class="techDataSubCol techDataSubColValue"><table cellpadding="0" cellspacing="0"><tbody><tr><td class="techDataSubCol techDataSubColValue">(.*?)</td></tr></tbody></table><div class="break"> <!-- break --> </div></td>',value)	
				
				result = "null"
				if len(keys) > 0:
					print "----------------------"
					print keys 
					print values
					result = {}
					for n in range(len(keys)):
						try:
							result.update({keys[n]:"hoi"})
						except IndexError:
							print "x"
				if result == "null":
					result = re.findall('<td class="techDataSubCol techDataSubColValue">(.*)</td></tr></table>', value)
				
				print tempkeys[i] 
				item['specs'].update({tempkeys[i]:result})
				
		yield item
