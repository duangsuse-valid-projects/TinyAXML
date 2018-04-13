# TinyAXML AndroidManifest 二进制文件序列化/反序列化库

此为 [AXMLEditor](https://github.com/fourbrother/AXMLEditor) 重写后的工具, 添加了 `dump/build/plugin` 指令(插件), 解决了那个工具的部分错误

实际上重构原项目工程量太大了... 而且有太多因为采用完全 _ser/deser_ 而弃用的代码和认为不够合适的代码, 故决定以那个工具的部分代码为基础重做一个新的库(实际上没用)

和那个工具一样, 本源采用 `Unlicensed` 许可证, 你爱怎么办怎么办, 本库的文档测试覆盖机会大概计划比那个高一点, 使用插件系统, 本身基本只包含 AXML 处理逻辑

顺便推荐 [一文章](https://blog.csdn.net/beyond702/article/details/51830108), 而且我把它复制成了 [gist](https://gist.github.com/duangsuse/3ae94e339eb188fa4ec8a87b6e105331) 方便参考

## 用途

针对于特定 __Android APK__ 反编译修改后无法执行回编译操作, 直接进行 __AXML__ 二进制文件修改, 然后只需要二次签名即可. 无需完全进行反编译和回编译操作

## 关于 `xmlpull` 依赖

原版包含了这个依赖以方便添加 __XML tag__, 如果 _insert_ 工具抛出 __NoClassDefFound__ 异常, 确认 __xmlpull.jar__ 与工具放在相同目录下(不存在的, 好像只是类路径)

## 什么是 `绝对名称`

简短版本: `<tag name="my_abs_name"></tag>`

在更新时原版的代码里, 部分方法对应的参数没有使用, 我在找到的 _XML_ 和 _AXML_ 文档里没有提到这个

## 用法

## Library usage

> A small library for serializing/deserializing Android binary AXML format

```java
import org.duangsuse.tinyaxml.AxmlFile

public class PrintResourceIds {
    public static void main(String[] args) {
        AxmlFile axml = new AxmlFile("AndroidManifest.xml");
        int i;
        for (int id:axml.resourceMap.ids) {
            System.out.println(String.format("#%1$i: %2$i", i++, id));
        }
    }
}
```

> Writing a plugin

```kotlin
import org.duangsuse.tinyaxml.AxmlFile

@JvmName("TinyAxmlPlugin")
public class PrintResourceIds() {
    public process(axml: AxmlFile, args: Array<String>) {
        var i: Int;
        for (int id:axml.resourceMap.ids) {
            System.out.println("#$i: $id");
        }
    }
}
```

> Run plugin: tinyaxml printResourceIds.class AndroidManifest.xml

### 方便的脚本

> sudo nano /usr/bin/tinyaxml

```bash
#!/bin/sh
export TINYAXML_PLUG=$HOME/.local/tinyaxml/
cd $HOME/Projects/tinyaxml/build/libs/
java -cp ../../:tinyaxml-*.jar org.duangsuse.tinyaxml.Main $*
```

> mkdir $HOME/.local/tinyaxml
> cd $HOME/Projects/tinyaxml/plugins; make install
> View JavaDoc for library documents

### 插入属性

`tinyaxml attr -i [标签名] [属性名] [属性值] [输入 axml] [输出 axml]`

示例： `tinyaxml attr -i application debuggable true AndroidManifest.xml`

在 _application_ 标签中插入 _android:debuggable="true"_ 属性, 让程序处于可调试状态

### 删除属性

`tinyaxml attr -r [标签名] [属性名] [输入 axml] [输出 axml]`

示例： `tinyaxml attr -r application allowBackup AndroidManifest.xml AndroidManifest_out.xml`

在 _application_ 标签中删除 _allowBackup_ 属性, 这样此 app 就可以进行沙盒数据备份

### 更改属性

`tinyaxml attr -m [标签名] [属性名] [属性值] [输入 axml] [输出 axml]`

示例： `tinyaxml attr -m application debuggable true AndroidManifest.xml AndroidManifest_out.xml`

在 _application_ 标签中修改 _android:debuggable_ 属性为 __true__, 让程序处于可调试状态

### 插入标签

`tinyaxml tag -i [需要插入标签内容的 xml 文件] [输入 axml] [输出 axml]`

示例： `tinyaxml tag -i inserting.xml AndroidManifest.xml AndroidManifest_out.xml`

因为插入标签时内容比较多, 命令行方式不方便, 直接输入一个需要插入标签内容的 xml 文件即可

### 删除标签

`tinyaxml tag -r [标签名] [输入 axml] [输出 axml]`

示例： `tinyaxml tag -r activity android.demo.MainActivity AndroidManifest.xml`

删除 __android:name="android.demo.MainActivity"__ 标签

### 序列化/反序列化 AXML

`tinyaxml dump [输入 axml]`

示例： `tinyaxml dump AndroidManifest.xml`

将 __AndroidManifest.xml__ 反序列化输出到 __AndroidManifest.xml.text__ 文本

`tinyaxml build [输入 serialized object] [输出 axml]`

示例： `tinyaxml build AndroidManifest.xml`

将 __AndroidManifest.xml.text__ 序列化输出到 __AndroidManifest.xml__

### 执行插件

`java -jar tinyaxml-*.jar [class name] [input file] (output file)`

__AXMLEditor__ 将反射加载初始化插件类, 调用其中的 _AxmlFile process(AxmlFile)_ 方法, 将结果序列化输出

#### 算法

tinyaxml 一般只接受长度在 _1_ 以上的参数 (至少有 plugin)

但其实它可以接受更多! 如果长度为 _0_:

```plain
如果 class name 以 .class 结尾, 检查文件是否存在 如果存在就是这个类, 否则
在插件目录 $TINYAXML_PLUG 下寻找 `missing_argument.class`
尝试加载这个类, 如果失败显示加载错误退出
尝试寻找类中的 public static void main(String[]) 方法, 以自己的 args 调用
如果失败显示插件方法查找错误退出
然后正常退出
```

如果长度为 1, 参数 1 为插件名称, 调用插件的 main 方法

如果长度为 2, 默认参数 1 是插件名, 2 是输入文件, 输入输出为同一文件

```plain
推断插件参数 or 输入文件：
检查 args[1] 是否是文件
如果不是即是插件参数, 调用 main 方法
```

如果长度为 3, 默认参数 1 是插件名, 2 和 3 分别是 I/O 文件

```plain
推断插件参数 or 输入文件：
检查 args[-2] 是否是文件
如果不是即是插件参数, 否则是输入文件
检查 args[-1] 是否是文件
如果不是即是插件参数, 否则是输出文件, 如果没有输入文件就作为输出文件
```

同时指定文件 I/O 或指定输入以使用默认推导, 如果没有写出就不使用 `process` 方法, 转而尝试 `main` 方法

如果没有找到 `AxmlFile process(AxmlFile, String[])` 或 `void process(AxmlFile, String[])`, 就使用 `argv[1..-1]` 调用插件类的 `void main(String[])` 方法

如果发现有无返回值的 `process` 方法, 不使用输出.

如果输出时发生错误, tinyaxml 会尝试标准输出 `System.out`
