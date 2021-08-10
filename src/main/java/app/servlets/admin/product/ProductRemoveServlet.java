package app.servlets.admin.product;

import app.service.ProductSearchService;
import app.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/product/remove")
public class ProductRemoveServlet extends HttpServlet {

    private static final ProductSearchService productSearchService = new ProductSearchService();
    private static final String MANAGE_PRODUCT_JSP = "/admin/product/manageProduct.jsp";
    private static final ProductService productService = new ProductService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String productIdToRemove = req.getParameter("productIdToRemove");

        if (productIdToRemove != null) {
            boolean isProductDeleted = productService.removeProduct(Integer.parseInt(productIdToRemove));
            req.setAttribute("isProductDeleted", isProductDeleted);
        }
        req.setAttribute("categoryWithProductsMap", productSearchService.getCategoryWithProductsMap());
        req.getRequestDispatcher(MANAGE_PRODUCT_JSP).forward(req, resp);
    }
}
