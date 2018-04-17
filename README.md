# GuideView:基于DialogFragment实现

基于`DialogFragment`实现的引导遮罩浮层视图，具备以下的特性：

* 响应导航按钮的动作（因为透明的浮层本质是一个dialog）
* 链式引导层，支持设定一组的引导遮罩视图，点击切换下一个试图
* 自动绘制半透明浮层、透明核心区以及确保目标视图和引导视图的位置

效果如下图所示：

![](/assets/guideview.gif)

## 实现说明

页面的结构如下图所示：

![ic_guideview](/assets/ic_guideview.png)

### 核心类

#### GuideViewBundle

引导视图的配置项类，每一页引导视图对应一个配置项。在**GuideView**内部通过这个配置项去构造`GuideView`的实例，并通过`GuideViewFragment`显示在界面上。

其中的属性都通过构造器的模式，通过静态内部类`Builder`进行构建，属性说明如下：

* targetView

  引导视图需要显示附着的目标视图

* hintView

  引导视图（不包含半透明浮层以及透明焦点区）

* transparentSpaceXXX

  默认的情况下，透明焦点区的大小跟目标视图的大小保持一致，如果需要加大透明区域的大小，可以通过设置这组属性，指定上下左右的额外的空白区域

* hintViewMarginXXX

  引导视图（hintView）相对于目标视图（targetView）的边距

* hasTransparentLayer

  是否显示透明焦点区域，默认显示。可以选择不绘制透明焦点区域而只有半透明的浮层

* hintViewDirection

  引导视图（hintView）相对于目标视图（targetView）的位置方向，目前可以定义上（上方左对齐）、下（下方左对齐）、左（左方上对齐）、右（右方上对齐）四个方向。如果需要在位置之余有不一样的对齐效果，可以使用hintViewMarginXXX属性

* outlineType

  透明焦点区的轮廓类型，有圆形（椭圆）轮廓和方形轮廓两种

* maskColor

  半透明遮罩浮层的颜色

* isDismissOnClicked

  全局点击可以关闭引导视图，默认为true。如果设置false，则需要手动设置点击hintView的特定位置关闭视图

#### GuideView

界面实际展示的视图对象，根据`GuideViewBundle`设置的属性，由`GuideViewFragment`创建并添加到齐视图容器中，对外部业务完全透明无感知到一个类

#### GuideViewFragment

实际显示引导视图的弹窗。其内部加载了一个`FrameLayout`容器，通过在容器中添加`GuideView`的实例实现显示引导视图层。一个`GuideViewFragment`可以设定一组引导视图，完成一组引导序列。请使用其静态内部类`Builder`构建其实例，并使用`Builder#addGuidViewBundle(bundle)`方法添加引导视图的配置项。

如果需要自定义点击关闭的动作（`GuideViewBundle.Builder#setDismissOnClicked(false)`的情况下），可以使用下面的方法

```Java
void onNext()
```

如果还存在没有显示的引导视图，这个方法会继续显示下一张，否则会关闭弹窗

### 使用示例

```kotlin
GuideViewFragment.Builder()
                    .addGuidViewBundle(GuideViewBundle.Builder()
                            .setTargetView(tvContent)
                            .setHintView(hintViewLeft)
                            .setDismissOnClicked(false)
                            .setHintViewMargin(0, -160, 0, 0)
                            .setTransparentSpace(space, space, space, space)
                            .setOutlineType(TYPE_RECT)
                            .setHintViewParams(params)
                            .setHintViewDirection(LEFT).build())
                    .addGuidViewBundle(GuideViewBundle.Builder()
                            .setTargetView(tvContent)
                            .setOutlineType(TYPE_OVAL)
                            .setHintView(hintViewTop)
                            .setDismissOnClicked(false)
                            .setHintViewParams(params)
                            .setHintViewMargin(-dp2px(this, 55f), 0, 0, 0)
                            .setTransparentSpace(space, space, space, space)
                            .setHintViewDirection(TOP)
                            .build())
                    .addGuidViewBundle(GuideViewBundle.Builder()
                            .setTargetView(tvContent)
                            .setOutlineType(TYPE_OVAL)
                            .setHintView(hintViewRight)
                            .setDismissOnClicked(false)
                            .setHintViewParams(params)
                            .setHintViewMargin(0, -160, 0, 0)
                            .setTransparentSpace(space, space, space, space)
                            .setHintViewDirection(RIGHT)
                            .build())
                    .addGuidViewBundle(GuideViewBundle.Builder()
                            .setTargetView(tvContent)
                            .setOutlineType(TYPE_OVAL)
                            .setHintViewParams(params)
                            .setHintViewMargin(-dp2px(this, 55f), 0, 0, 0)
                            .setHintView(hintViewBottom)
                            .setTransparentSpace(space, space, space, space)
                            .setHintViewDirection(BOTTOM)
                            .build())
                    .setCancelable(false)
                    .build().show(supportFragmentManager, "hit")
```



