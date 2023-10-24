@ResponseBody，直接将方法返回值序列化成json返回给客户端
@RequestMapping，给整个控制器设置一个基本url
@RequestParam，从http请求获取参数值，作用于方法参数上


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")，json中有一个type属性标识对象的具体类型
@JsonCreator，标记一个构造方法，指示反序列化时应该使用的方法
@JacksonInject，指示反序列化时应该注入的特定的值

ComparisonChain 是 Google Guava 库中的一个类，用于创建链式比较器，帮助你进行多个字段的比较。通过 ComparisonChain，你可以按顺序比较多个字段，并在找到不等的情况下立即返回结果，而不必一直比较所有字段。