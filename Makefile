total_stats: stats.edn.gz
	zcat $^ | pv | grep ":total {[^}]*}" -o | sort | uniq -c > total_stats
