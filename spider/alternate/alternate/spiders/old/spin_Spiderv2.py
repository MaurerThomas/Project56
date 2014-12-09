#coding: utf8 
from urlparse import urljoin

from scrapy.spider import Spider
from scrapy.selector import Selector
from alternate.items import AlternateItem
from scrapy.http import Request
import re

class spin_Spider(Spider):
	name = "spin"
	allowed_domains = ["alternate.nl"] 
	start_urls = [
		      "https://www.alternate.nl/html/product/listing.html?filter_5=&filter_4=&filter_3=&filter_2=&filter_1=&size=1000&bgid=8148&lk=9309&tk=7&navId=2436", #behuizingen
		      "https://www.alternate.nl/html/product/listing.html?size=1000&lk=13472&tk=7&navId=20678", #DDR4
		      "https://www.alternate.nl/html/product/listing.html?filter_5=&filter_4=&filter_3=&filter_2=&filter_1=&size=1000&bgid=8296&lk=9326&tk=7&navId=11556", #DDR3
                      "https://www.alternate.nl/html/product/listing.html?filter_5=&filter_4=&filter_3=&filter_2=&filter_1=&size=1000&lk=9312&tk=7&navId=11554", #DDR2
		      "https://www.alternate.nl/html/product/listing.html?navId=11542&tk=7&lk=9335", #DDR
 		      "https://www.alternate.nl/html/product/listing.html?navId=11558&tk=7&lk=9324", #SDRAM
		      "https://www.alternate.nl/html/product/listing.html?navId=17362&tk=7&lk=9518", #Geluidskaarten PCI
                      "https://www.alternate.nl/html/product/listing.html?navId=17363&tk=7&lk=9519", #Geluidskaarten PCIe
					"https://www.alternate.nl/html/product/listing.html?navId=17364&tk=7&lk=9520", #Geluidskaarten USB
                      "https://www.alternate.nl/html/product/listing.html?filter_5=&filter_4=&filter_3=&filter_2=&filter_1=&size=500&bgid=11369&lk=9374&tk=7&navId=11606",#grafische kaarten PCIe NVIDIA
					  "https://www.alternate.nl/html/product/listing.html?navId=11608&bgid=10846&tk=7&lk=9365", #Grafische kaarten PCIe ATI/AMD
                      "https://www.alternate.nl/html/product/listing.html?navId=1358&tk=7&lk=9372", #Grafische kaarten - PCIe Matrox
                      "https://www.alternate.nl/html/product/listing.html?navId=1360&tk=7&lk=9381", #Grafische kaarten - AGP kaarten
                      "https://www.alternate.nl/html/product/listing.html?navId=1362&tk=7&lk=9361", #Grafische kaarten - PCI kaarten
                      "https://www.alternate.nl/html/product/listing.html?filter_5=&filter_4=&filter_3=&filter_2=&filter_1=&size=500&bgid=8459&lk=9563&tk=7&navId=11584", #Harde schijven intern - SATA
                      "https://www.alternate.nl/html/product/listing.html?navId=990&navId=988&tk=7&lk=9553", #Harde schijven intern - SAS - 2,5 inch
                      "https://www.alternate.nl/html/product/listing.html?navId=17557&bgid=8459&tk=7&lk=9601", #Harde schijven intern - Hybride
                      "https://www.alternate.nl/html/product/listing.html?navId=11890&bgid=8985&tk=7&lk=9585", #SSD - SATA
                      "https://www.alternate.nl/html/product/listing.html?navId=14655&bgid=8985&tk=7&lk=9599", #SSD - mSATA
                      "https://www.alternate.nl/html/product/listing.html?navId=19991&tk=7&lk=12801", #SSD - M.2
                      "https://www.alternate.nl/html/product/listing.html?navId=1690&tk=7&lk=9590", #SSD - PCI Express
                      "https://www.alternate.nl/html/product/listing.html?navId=11898&bgid=8215&tk=7&lk=9344", #Koeling - CPU koelers
                      "https://www.alternate.nl/html/product/listing.html?navId=11570&tk=7&lk=9351", #Koeling - Waterkoeling
                      "https://www.alternate.nl/html/product/listing.html?navId=11898&tk=7&lk=9493", #Koeling - CPU koelers
                      "https://www.alternate.nl/html/product/listing.html?navId=11622&tk=7&lk=9419", #Moederborden - AMD
                      "https://www.alternate.nl/html/product/listing.html?navId=11626&tk=7&lk=9435",#Moederborden - Intel
                      "https://www.alternate.nl/html/product/listing.html?navId=17480&tk=7&lk=9452", #Moederborden - Overige - Geïntegreerde CPU
                      "https://www.alternate.nl/html/product/listing.html?navId=11572&bgid=10846&tk=7&lk=9487", #Processor - Desktop
                      "https://www.alternate.nl/html/product/listing.html?navId=11604&bgid=8215&tk=7&lk=9533" #Voeding
                      ]
	def parse (self,response):
		
		products = response.xpath('//*[starts-with(@class,"productLink")]')
		items = []
		for product in products:
			item = AlternateItem()
			link = product.xpath('@href').extract()
			array = []
			if link:
				
				#item['component'] = scrapy.Request(url=link[0], meta={'item': item}, callback=self.parse_item)
				request = Request(url=link[0], meta={'item': item}, callback=self.parse_item)
				request.meta['item'] = item
				
				array.append(request)
				print item
			item["component"] = array
			items.append(item)
		yield items
			
			
		
	def parse_item(self,response):
		item = response.meta['item']
		datalist = response.xpath('//*[starts-with(@class,"techData")]')
		title = datalist.xpath('//*[starts-with(@class,"breadCrumbs")]/span/a/span/text()').extract()
		title = title[1]
		name = datalist.xpath('//*[starts-with(@class,"productNameContainer")]/*[starts-with(@itemprop,"name")]/text()').extract()
		brand = datalist.xpath('//*[starts-with(@class,"productNameContainer")]/*[starts-with(@itemprop,"brand")]/text()').extract()
		
		print title
		itemarray ={}
		itemarray[title] = {"name":name,"brand":brand}
		for data in datalist:
			tempkeys = data.xpath('//*[starts-with(@class,"techDataCol1")]/text()').extract()
			tempvalue = data.xpath('//*[starts-with(@class,"techDataCol2")]/text()').extract()
			if(tempkeys):
				for i in range(len(tempkeys)):
					itemarray[title].update({tempkeys[i]:"hoi"})
		#item.append(itemarray)
		return itemarray
	
	
	
