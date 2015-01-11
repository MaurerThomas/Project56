#coding: utf8 
from scrapy.spider import Spider
from scrapy.selector import Selector
from prijscrawler.items import AlternateItem
import re

class AlternateSpider(Spider):
	name = "alternate"
	allowed_domains = ["alternate.nl"] 
	start_urls = [
						"https://www.alternate.nl/html/product/listing.html?filter_5=&filter_4=&filter_3=&filter_2=&filter_1=&size=1000&bgid=8148&lk=9309&tk=7&navId=2436", #behuizingen
						"https://www.alternate.nl/html/product/listing.html?size=1000&lk=13472&tk=7&navId=20678", #DDR4
						"https://www.alternate.nl/html/product/listing.html?filter_5=&filter_4=&filter_3=&filter_2=&filter_1=&size=1000&bgid=8296&lk=9326&tk=7&navId=11556", #DDR3
						"https://www.alternate.nl/html/product/listing.html?filter_5=&filter_4=&filter_3=&filter_2=&filter_1=&size=1000&lk=9312&tk=7&navId=11554", #DDR2
						"https://www.alternate.nl/html/product/listing.html?navId=11542&tk=7&lk=9335", #DDR
						"https://www.alternate.nl/html/product/listing.html?navId=11558&tk=7&lk=9324", #SDRAM
						"https://www.alternate.nl/html/product/listing.html?filter_5=&filter_4=&filter_3=&filter_2=&filter_1=&size=500&bgid=11369&lk=9374&tk=7&navId=11606",#grafische kaarten PCIe ATI/AMD
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
						"https://www.alternate.nl/html/product/listing.html?navId=756&tk=7&lk=9359",#Koeling - HDD  koelers
						"https://www.alternate.nl/html/product/listing.html?navId=11568&bgid=8215&tk=7&lk=9346", #Koeling - Behuizing koelers
						"https://www.alternate.nl/html/product/listing.html?navId=758&tk=7&lk=9350", #Koeling - Geheugen koelers
						"https://www.alternate.nl/html/product/listing.html?navId=11570&tk=7&lk=9351", #Koeling - Waterkoeling
						"https://www.alternate.nl/html/product/listing.html?navId=11622&tk=7&lk=9419", #Moederborden - AMD
						"https://www.alternate.nl/html/product/listing.html?navId=11626&tk=7&lk=9435",#Moederborden - Intel
						"https://www.alternate.nl/html/product/listing.html?navId=11572&bgid=10846&tk=7&lk=9487", #Processor - Desktop
						"https://www.alternate.nl/html/product/listing.html?navId=11898&tk=7&lk=9493", #Koeling - CPU koelers
						"https://www.alternate.nl/html/product/listing.html?navId=11604&bgid=8215&tk=7&lk=9533" #Voeding
                      ]
	def parse (self,response):
		
		products = response.xpath('//*[starts-with(@class,"listRow")]')
		items = []
		for product in products:
			item = AlternateItem()
			temp = product.xpath('a/span/span/h2/*[starts-with(@class,"name")]/span/text()').extract()
			item['url'] = product.xpath('a/@href').extract()
			tempeuro = product.xpath('div/p/*[starts-with(@class,"price right")]/text()').extract()
			item['euro'] = re.findall(r'\d+', ''.join(tempeuro))
			tempcent = product.xpath('div/p/span/sup/text()').extract()
			item['cent'] = re.findall(r'\d+', ''.join(tempcent))
			items.append(item)			
		return items
