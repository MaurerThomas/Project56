echo $(date) > timestamps.txt
scrapy crawl spin -o items.json
echo $(date) > timestamps.txt
