检查是否支持SSE4.2指令集，向量化需要该特性
grep -q sse4_2 /proc/cpuinfo && echo "SSE 4.2 supported" || echo "SSE 4.2 not supported"