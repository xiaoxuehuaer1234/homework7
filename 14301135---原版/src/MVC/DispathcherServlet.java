package MVC;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DispathcherServlet extends HttpServlet {

	ModelAndView mav;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ����doGet����
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		PrintWriter out = response.getWriter();
		out.println("hello world");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// �����ύpost�����󣬸�����������
		// �ȰѶ�Ӧcontroller�е�mapping�ҵ�������������֮��
		mav = new ModelAndView();
		String name=request.getParameter("name");
		String pas=request.getParameter("pas");
		mav.addMap("name", name);
		mav.addMap("pas", pas);
		
		loadController(request.getServletPath());
		
		Obj[] obj = mav.getObjects();
		for(int i=0;i<obj.length;i++)
			request.setAttribute(obj[i].getName(), obj[i].getObj());
		request.getRequestDispatcher(mav.getViewName()).forward(request, response);

	}

	// ʵ��@controller
	private void loadController(String url) {
		String packageName = "";
		File root = new File("G:\\Towyer\'sSE\\JavaEE\\MVC\\src");

		try {
			loadJava(root, packageName, url);
		} catch (Exception e) {
			System.out.println("error");
			e.printStackTrace();
		}
	}

	private void loadJava(File folder, String packageName, String url) throws Exception {
		File[] files = folder.listFiles();
		for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
			File file = files[fileIndex];
			if (file.isDirectory()) {
				loadJava(file, packageName + file.getName() + ".", url);
			} else {
				// ��ÿ��java�������ƥ��controller
				String filename = file.getName();

				try {
					String name = filename.substring(0, filename.length() - 5);
					Class<?> obj = Class.forName(packageName + name);
					// �ҵ�component

					if ((Controller) obj.getAnnotation(Controller.class) != null) {

						Controller com = (Controller) obj.getAnnotation(Controller.class);
						// �ҵ�controller�󣬲���url��Ӧ����

						findRequestMapping(url, obj);

					}
				} catch (Exception e) {
					System.out.println("error");
				}
			}
		}
	}

	// ʵ��@@RequestMapping
	public Object findRequestMapping(String url, Class<?> obj1) {

		Class<?> obj = obj1;
		Method[] ms = obj.getMethods();

		// ����ÿ�� method ����ע�����
		for (Method m : ms) {

			if ((RequestMapping) m.getAnnotation(RequestMapping.class) != null) {
				RequestMapping rm = (RequestMapping) m.getAnnotation(RequestMapping.class);
				if (url.equals(rm.value())) {
					try {
						
						Object arglist[] = new Object[1];
						arglist[0] = mav;
						
						mav = (ModelAndView) m.invoke(obj.newInstance(), arglist);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

}
