[![Maven Central Version](https://img.shields.io/maven-central/v/dev.fastball/fastball-parent)](https://central.sonatype.com/artifact/dev.fastball/fastball-parent/)


# fastball
An extension-oriented Wysiwyg UI framework for Java backends. Save your development time, try Fastball!

Fastball 是一套面向后端研发, 声明式界面开发框架. 核心定位是让常规后端人员可以无前端知识的前提下, 完成 Web 前端界面开发.

并允许专业前端介入, 可完成复杂界面交互以及组件扩展, 从而减少前后端环节的信息损耗以及研发成本.

## 原理概述
常见的 Web 软件, 从前后端交互的视角来看, 我们可以将其分为可视化的人机交互界面, 以及数据操作的接口服务组成.

我们认为在模式化的场景中, 数据操作的接口服务, 一定程度上可以描述人机交互界面, 因此可以考虑通过增强接口描述的方式, 来达到模式化界面的生成, 并加强前后端的联调协同性, 以此来提升 Web 软件的研发效率.

而且在 ToB 的企业软件行业, 模式化场景的占比会更高, 因此提出数据接口主导界面假设.

## 接口主导界面假设
而 Web 软件在生长的过程中, 如果业务有所变化, 抛开纯界面风格的变化外, 有以下几点假设:

1. 界面上需要展现的数据交互, 需要后端接口支撑才能实现
2. 存在一些接口逻辑变化, 而前端无需修改的情况, 最典型的比如变换业务查询条件, 这是无需界面感知的变化.
3. 如果界面修改, 接口大概率也需要修改, 这是因为业务变化多数也会带来额外的信息获取或者提交诉求, 这些变化在不修改接口的情况下很难实现, 即 `交互界面很难脱离接口服务独立完成`. 
4. 校验规则多数情况下界面与接口一致, 少数情况下界面大于接口.
5. 在前后端协同上来说, 前端属于使用者, 而后端是提供者, 例如接口的定义, 出入参的格式等. (GQL 这种暂不讨论, 其实本质上前端也仍然是使用者)

基于上述假设, 我们可以看到很多信息是后端服务接口定义, 而前端界面配合的方式在运作. 当然, 从业务的角度可能是产品原型驱动界面设计, 而界面设计驱动接口设计, 不过这是需求阶段, 在实现阶段仍然是后端接口供前端使用的模式.

另外, Web 系统中, 人机交互界面也存在一定模式化的现象, 这部分前端工作更多的是在处理字段以及接口的适配, 特别是 ToB 方向的企业软件中的中后台管理系统.

因此我们可以考虑将模式化的界面, 转化为标准的后端接口, 由后端接口产出模式化的界面, 也包括校验规则等交互要素. 同时提供 `前端的二次开发以及调整的能力`, 这样在非模式化, 或者在模式化边缘的一些场景, 也能够由前端自由发挥来得以满足.