package com.lwy.myProxy.proxy;

import com.lwy.myProxy.util.MyInvocationHandler;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 手写动态代理，生成代理对象，同时让代理逻辑动态
 */
public class MyProxyNew {

    /**
     * 手动生成代理对象的java文件，然后编译成class文件，然后new一个代理对象
     * 代理对象和目标对象实现同一个接口
     *
     * @param targetInterface     目标对象实现的接口
     * @param myInvocationHandler 代理逻辑类实现的接口
     * @return
     */
    public static Object newInstance(Class targetInterface, MyInvocationHandler myInvocationHandler) {

        // 生成的代理对象
        Object proxy = null;

        // 目标接口的名字
        String interfaceName = targetInterface.getSimpleName();

        // Handler的名字
        String handlerName = MyInvocationHandler.class.getSimpleName();

        // 获取目标接口中的方法--需要被代理增强的方法
        Method methods[] = targetInterface.getDeclaredMethods();

        // 换行符
        String otherLine = "\n";

        // Tab，缩进四个空格
        String tab = "\t";

        // java文件的内容
        String content = "";

        // java文件所属包的内容
        String packageContent = "package com.lwy;" + otherLine + otherLine;

        // java文件导入jar包的内容 targetInterface.getName()获取全类名--带包名
        String importContent = "import " + targetInterface.getName() + ";" + otherLine
                + "import " + MyInvocationHandler.class.getName() + ";" + otherLine
                + "import java.lang.reflect.Method;" + otherLine
                + "import java.lang.Exception;" + otherLine;

        // java内容的第一行，定义一个类，类名$Proxy
        String classFirstLineContent = "public class $Proxy implements " + interfaceName + " {" + otherLine + otherLine;

        // 定义属性
        String filedContent = tab + "private " + handlerName + " myInvocationHandler;" + otherLine + otherLine;

        // 定义构造方法
        String constructorContent = tab + "public $Proxy (" + handlerName + " myInvocationHandler){"
                + otherLine + tab + tab + "this.myInvocationHandler = myInvocationHandler;" + otherLine + tab + "}" + otherLine + otherLine;

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

            // 调用getDeclared方法所传参数列表
            String getDeclaredParamsContent = "";

            // 调用invoke方法所传参数列表
            String invokeParamsContent = "";

            // 参数列表中是否有类需要导入
            String importArgsContent = "";

            // 存在参数列表的情况下，拼接参数列表
            if (args.length > 0) {

                for (int i = 0; i < args.length; i++) {
                    // 目标方法参数类型
                    String argType = args[i].getSimpleName();

                    // 生成样式：String param1, String param2
                    argsContent += argType + " param" + i + ",";

                    // 生成样式：String.class, String.class
                    getDeclaredParamsContent += argType + ".class,";

                    // 生成样式：param0,param1
                    invokeParamsContent += "param" + i + ",";

                    // 参数列表中是否有类需要导入
                    importArgsContent = "import " + args[i].getName() + ";";

                    if (!importContent.contains(importArgsContent)) {
                        importContent += importArgsContent + otherLine;
                    }


                }

                // 把最后一位多余的,去掉
                argsContent = argsContent.substring(0, argsContent.length() - 1);
                getDeclaredParamsContent = getDeclaredParamsContent.substring(0, getDeclaredParamsContent.length() - 1);
                invokeParamsContent = invokeParamsContent.substring(0, invokeParamsContent.length() - 1);

            }


            // 调用MyInvocationHandler的invoke方法，invoke里通过反射调用目标方法
            // 调用invoke方法，参数args格式
            String invokeArgsContent = tab + tab + "Object[] args = {" + invokeParamsContent + "};";

            // 调用getDeclaredMethod参数args格式
            String getDeclaredMethodArgsContent = "Class[] classArgs = {" + getDeclaredParamsContent + "};";

            // 调用invoke方法，参数method格式（其实就是获取目标方法）
            String invokeMethodContent = tab + tab + "Method method = null;" + otherLine
                    + tab + tab + "try {" + otherLine + tab + tab + tab
                    + "method = Class.forName(\"" + targetInterface.getName()
                    + "\").getDeclaredMethod(\"" + methodName + "\", classArgs);" + otherLine + tab + tab
                    + "} catch (Exception e) {" + otherLine + tab + tab + tab + "e.printStackTrace();"
                    + otherLine + tab + tab + "}" + otherLine;

            if (methodReturnType.equals("void")) { // 没有返回值情况下

                methodContent += tab + "public " + methodReturnType + " " + methodName + " (" +
                        argsContent + ") {" + otherLine + invokeArgsContent + otherLine
                        + tab + tab + getDeclaredMethodArgsContent + otherLine + invokeMethodContent
                        + tab + tab + "myInvocationHandler.invoke(method, args);" + otherLine + tab
                        + "}" + otherLine;

            } else { // 有返回值情况下

                // 判断是否已经导入过返回类型的包
                String importString = "import " + method.getReturnType().getName() + ";";

                if (!importContent.contains(importString)) {

                    importContent += importString + otherLine;

                }

                methodContent += tab + "public " + methodReturnType + " " + methodName + " (" +
                        argsContent + ") {" + otherLine + invokeArgsContent + otherLine
                        + tab + tab + getDeclaredMethodArgsContent + otherLine + invokeMethodContent
                        + tab + tab + "return (" + methodReturnType + ")myInvocationHandler.invoke(method, args);"
                        + otherLine + tab + "}" + otherLine;

            }

        }

        // 拼接java文件内容
        content = packageContent + importContent + classFirstLineContent + filedContent
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

            Constructor constructor = clazz.getConstructor(MyInvocationHandler.class);
            proxy = constructor.newInstance(myInvocationHandler);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return proxy;

    }

}
