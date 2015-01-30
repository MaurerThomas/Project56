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
		cursor.execute("""SELECT url FROM url_ean WHERE url LIKE '%alternate%' """)
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
		item['specs'] ={}	
		
		datalist = response.xpath('//*[@class="techData"]')
		component = datalist.xpath('//*[@id="contentWrapper"]/div[1]/span[2]/a/span/text()').extract()[0]
		if "Moederbord" in component:
			item = self.getSpecs( datalist, item, "c1" ,"c4")
		elif "Geheugen" in component:
			item = self.getSpecs( datalist, item, "c1" ,"c4")
		else:
			item = self.getSpecs( datalist, item, "techDataCol1" ,"techDataCol2")
			
		item['specs'].update(self.getSpecifics(response))
		cursor = self.conn.cursor()
		try:
			cursor.execute("""UPDATE url_ean SET ean = %s WHERE url = %s""", (item['specs']['ean'], response.url))
			self.conn.commit()
			
		except MySQLdb.Error, e:
			print "Error %d: %s hoi" % (e.args[0], e.args[1])
		cursor.close()
		return item

	def getSpecs(self, datalist, item, firststring, secondstring):
		
		if firststring == "techDataCol1":
			tempkeys = datalist.xpath('//*[@class="'+firststring+'"]/text()').extract()
			tempvalue = datalist.xpath('//*[@class="'+secondstring+'"]')
			if(tempkeys):
				
				for i in range(len(tempkeys)):
					tempkeys[i] = re.sub("[.]",",",tempkeys[i])
					value = tempvalue[i].extract()
					result = "null"
					keys = re.findall('techDataSubCol techDataSubColDescription">(.*?)</td><td class="techDataSubCol techDataSubColValue"><table cellpadding="0" cellspacing="0"><tr><td class="techDataSubCol techDataSubColValue">' ,value)
					values = re.findall('<td class="techDataSubCol techDataSubColValue"><table cellpadding="0" cellspacing="0"><tr><td class="techDataSubCol techDataSubColValue">(.*?)</td></tr></table>',value)	
					for key in keys:
						key = re.sub("[.]",",",key)
					
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
						
					if "Vermogen" in item["specs"]:
						item['specs']['Vermogen'] = re.findall(r'\d+', ''.join(item['specs']['Vermogen']))[0]
		else :
			tablerows = datalist.xpath('table/tr')
			for row in tablerows:
				key = row.xpath('*[@class="'+firststring+'"]/text()').extract()
				value = row.xpath('*[@class="'+secondstring+'"]/text()').extract()
				try:
					item['specs'].update({key[0] : value[0] })
				except:
					random = "dit is leuke ding"
		return item
	
	def getEan(self, response):
		scriptlist = response.xpath('//*[@id="buyProduct"]/script/text()').extract()
		ean = ""
		for script in scriptlist:
			if "ccs_cc_args" in "".join(script):
				ean = (''.join(script)).split("upcean")[1]
				ean = re.findall(r'\d+', ''.join(ean))[0]
				if(len(ean) < 5):
					ean = ""
		return ean
		
	def getSpecifics(self, response):
	
		ean = self.getEan(response)
		breadcrumbs = response.xpath('//*[@id="contentWrapper"]/div[1]').extract()
		component = response.xpath('//*[@id="contentWrapper"]/div[1]/span[2]/a/span/text()').extract()[0]
		if "Voedingen" in "".join(breadcrumbs):
			component = response.xpath('//*[@id="contentWrapper"]/div[1]/span[3]/a/span/text()').extract()[0]
		naam = response.xpath('//*[@id="buyProduct"]/div[1]/h1/span[2]/text()').extract()[0]
		naam = re.sub('(?i)' + re.escape(component), '', naam) 
		naam = re.sub('(?i)' + re.escape(component.rstrip()[:-2]), '', naam) 
		naam = re.sub(',', '', naam) 
		specifics = {"component": component,
							"merk":response.xpath('//*[@id="buyProduct"]/div[1]/h1/span[1]/text()').extract()[0],
							"naam":naam,
							"ean":ean,
							"url": response.url,
							}
		return specifics					

			

		