# MRJava
刚做移动端测试时折腾的一个东西，重新封装 chimpchat，调用 ddmlib，结合 Adb-For-Test，扩展 Monkeyrunner

## 
README 后续补充。代码很烂，自己都不敢看。  
功能基本上封装完成

*	低于 4.1 版本，且开启 View Server , 可使用 View 这个类，通过 id 进行元素定位
*	高于 4.1 版本， 可使用 Element 类，通过 id、class、text 等属性定位

熟悉 java 的话，具体可参考里面的 Unit Test Case。
