@JsonTypeInfo: 用于指定在序列化和反序列化过程中如何包含类型信息。use = JsonTypeInfo.Id.NAME 表示通过字段名（这里是 "type"）包含类型信息。
@JsonSubTypes: 用于指定子类型的信息，表示父类中可能包含的子类型，并为每个子类型分配一个唯一的名称。