package app.repositories;

import app.entities.Product;
import org.apache.commons.lang3.ObjectUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ProductRepository extends DAO<Product> {

    private static final String GET_ALL_STATEMENT = "SELECT * FROM products";
    private static final String BASE_SEARCH_QUERY = "select products.*, product_category.category_name from products join product_category on products.product_category_id = product_category.category_id";
    private static final String DELETE_FROM_PRODUCTS_BY_ID_STATEMENT = "DELETE from products WHERE product_id = ?";
    private static final String GET_BY_CATEGORY_ID = "SELECT * FROM products WHERE product_category_id = ?";

    @Override
    public boolean insert(Connection connection, Product objectToInsert) throws SQLException {
        return false;
    }

    @Override
    public boolean update(Connection connection, Product objectToUpdate) throws SQLException {
        return false;
    }

    @Override
    public Product getById(Connection connection, int id) throws SQLException {
        return null;
    }

    @Override
    public boolean delete(Connection connection, Product objectToDelete) throws SQLException {
        return false;
    }

    public boolean deleteById(Connection connection, int productId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_FROM_PRODUCTS_BY_ID_STATEMENT);
        preparedStatement.setInt(1, productId);
        return preparedStatement.executeUpdate() == 1;
    }
    public List<Product> getProductsByCategoryId(Connection connection, int productCategoryId) throws SQLException {
        List<Product> productList = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_CATEGORY_ID);
        preparedStatement.setInt(1, productCategoryId);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                productList.add(mapProduct(resultSet));
            }
        }
        return productList;
    }

    @Override
    public List<Product> getAll(Connection connection) throws SQLException {
        List<Product> productList = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_STATEMENT);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                productList.add(mapProduct(resultSet));
            }
        }
        return productList;
    }

    public List<Product> search(Connection connection, String category, String name) throws SQLException {
        List<Product> productList = new ArrayList<>();
        PreparedStatement preparedStatement = buildSearchStatement(connection, category, name);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                productList.add(mapProduct(resultSet));
            }
        }
        return productList;
    }

    private PreparedStatement buildSearchStatement(Connection connection, String category, String name) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder(BASE_SEARCH_QUERY);
        if (hasAnySearchParams(category, name)) {
            queryBuilder.append(" WHERE ");
            if (isNotBlank(category)) {
                queryBuilder.append("category_name like ? ");
            }
            if (isNotBlank(name)) {
                queryBuilder.append(" and ");
                queryBuilder.append("product_name like ? ");
            }
            PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString());
            int paramIndex = 1;
            if (isNotBlank(category)) {
                preparedStatement.setString(paramIndex, "%" + category + "%");
                paramIndex++;
            }
            if (isNotBlank(name)) {
                preparedStatement.setString(paramIndex, "%" + name + "%");
            }
            return preparedStatement;

        }
        return connection.prepareStatement(BASE_SEARCH_QUERY);
    }

    private boolean hasAnySearchParams(String category, String name) {
        return ObjectUtils.anyNotNull(category, name);
    }


    private Product mapProduct(ResultSet resultSet) throws SQLException {
        return new Product(resultSet.getInt("product_id"),
                resultSet.getString("product_category_id"),
                resultSet.getString("product_name"),
                resultSet.getString("product_price"),
                resultSet.getString("product_description"));
    }
}

