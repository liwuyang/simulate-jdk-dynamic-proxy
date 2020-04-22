package com.lwy.myProxy.proxy;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 手写动态代理，生成代理对象--自己写了返回那一部分
 */
public class MyProxy {

    /**
     * 手动生成代理对象的java文件，然后编译成class文件，然后new一个代理对象
     * 代理对象和目标对象实现同一个接口
     *
     * @param target 目标对象
     * @return proxy 代理对象
     */
    public static Object newInstance(Object target) {

        // 定义代理对象
        Object proxy = null;

        // 获取目标对象实现的目标接口，暂时只实现了一个接口，所以取第一个
        Class targetInterface = target.getClass().getInterfaces()[0];

        // 目标接口的名字
        String interfaceName = targetInterface.getSimpleName();

        // 获取目标接口中的方法--需要被代理增强的方法
        Method methods[] = targetInterface.getMethods();

        // 换行符
        String otherLine = "\n";

        // Tab，缩进四个空格
        String tab = "\t";

        // java文件的内容
        String content = "";

        // java文件所属包的内容
        String packageContent = "package com.lwy;" + otherLine + otherLine;

        // java文件导入jar包的内容 targetInterface.getName()获取全类名--带包名
        String importContent = "import " + targetInterface.getName() + ";" + otherLine;

        // java内容的第一行，定义一个类，类名$Proxy
        String classFirstLineContent = "public class $Proxy implements " + interfaceName + " {" + otherLine + otherLine;

        // 定义目标对象
        String targetContent = tab + "private " + interfaceName + " target;" + otherLine + otherLine;

        // 定义构造方法
        String constructorContent = tab + "public $Proxy (" + interfaceName + " target){"
                + otherLine + tab + tab + "this.target = target;" + otherLine + tab + "}" + otherLine + otherLine;

        // 重写目标接口中的方法
        String methodContent = "";

        // 循环目标接口中的目标方法，依次重写然后拼接
        for (Method method : methods) {

            // 目标方法名称
            String methodName = method.getName();

            // 目标方法返回类型
            String methodReturnType = method.getReturnType().getSimpleName();

            // 目标方法参数
            Class args[] = method.getParameterTypes();

            // 重写后方法的参数列表
            String argsContent = "";

            // 调用目标方法所传参数列表
            String paramsContent = "";

            // 反射调用方法的参数列表
            Object[] argsInvoke = {};
            List argsInvokeList = new ArrayList(Arrays.asList(argsInvoke));

            // 存在参数列表的情况下，拼接参数列表
            if (args.length > 0) {

                for (int i = 0; i < args.length; i++) {
                    // 目标方法参数类型
                    String argType = args[i].getSimpleName();

                    // 生成样式：String param1, String param2
                    argsContent += argType + " param" + i + ",";

                    // 生成样式：param1, param2
                    paramsContent = "param" + i + ",";

                    argsInvokeList.add("param" + i);
                }

                // 把最后一位多余的,去掉
                argsContent = argsContent.substring(0, argsContent.length() - 1);
                paramsContent = paramsContent.substring(0, paramsContent.length() - 1);

                // 参数list转换为数组
                argsInvoke = argsInvokeList.toArray();

            }

            // 目标方法返回内容
            String returnContent = "";

            // 判断目标方法有无返回值，有返回值情况下，获取返回值
            if (!methodReturnType.equals("void")) {
                try {
                    // 通过反射获调用目标方法获取到返回值 还没看jdk底层源码，自己只想到这么做
                    if (methodReturnType.equals("String")) { // String情况下，把引号拼接上
                        returnContent = "return " + "\"" + method.invoke(target, argsInvoke) + "\";";
                    } else { // 返回值是对象
                        // 获取目标方法返回值类型
                        Class methodReturnTypeClass = method.getReturnType();
                        // import新增导入包
                        importContent += "import " + methodReturnTypeClass.getName() + ";" + otherLine;

                        // 获取返回对象
                        Object returnObject = method.invoke(target, argsInvoke);

                        // 根据返回对象获取字符串 User{name='lwy'}
                        String returnObjectString = returnObject.toString();

                        // 有属性值的情况下
                        if (returnObjectString.length() > methodReturnType.length() + 2) {

                            // 截取属性内容 获得name='lwy'
                            returnObjectString = returnObjectString.substring(methodReturnType.length() + 1, returnObjectString.length() - 1);

                            // 将属性以及赋值转换为数组 {name='lwy'}
                            String[] argsValue = returnObjectString.split(",");

                            // 所有属性值
                            String valueContent = "";

                            // 将属性值取出
                            for (int i = 0; i < argsValue.length; i++) { // 只解决了参数类型是String的
                                String[] value = argsValue[i].split("=");
                                valueContent += value[1].replace("\'", "\"") + ",";
                            }

                            if (valueContent.length() > 0) {
                                // 把最后一位多余的,去掉
                                valueContent = valueContent.substring(0, valueContent.length() - 1);
                            }

                            returnContent = "return new " + methodReturnType + "(" + valueContent + ");";

                        } else {

                            returnContent = "return new " + methodReturnType + "();";

                        }

                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            // 这里，代理逻辑写死，只是打印了一行内容，也可以写成动态的，但是太麻烦
            methodContent += tab + "public " + methodReturnType + " " + methodName + " (" +
                    argsContent + ") {" + otherLine + tab + tab + "" + "System.out.println(\"--------------log-------------\");"
                    + otherLine + tab + tab + "target." + methodName + "(" + paramsContent + ");"
                    + otherLine + tab + tab + returnContent + otherLine + tab + "}" + otherLine + otherLine;


        }

        // 拼接java文件内容
        content = packageContent + importContent + classFirstLineContent + targetContent
                + constructorContent + methodContent + "}";

        // 将文件内容写入一个java文件
        File file = new File("E:\\com\\lwy\\$Proxy.java");

        try {

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();

            // 将java文件编译成class
            JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileMgr = javaCompiler.getStandardFileManager(null, null, null);
            Iterable units = fileMgr.getJavaFileObjects(file);
            JavaCompiler.CompilationTask t = javaCompiler.getTask(null, fileMgr, null, null, null, units);
            t.call();
            fileMgr.close();

            // 加载这个class，生成代理对象
            URL[] urls = new URL[]{new URL("file:E:\\\\")};
            URLClassLoader urlClassLoader = new URLClassLoader(urls);
            Class clazz = urlClassLoader.loadClass("com.lwy.$Proxy");

            Constructor constructor = clazz.getConstructor(targetInterface);

            proxy = constructor.newInstance(target);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return proxy;

    }

}
