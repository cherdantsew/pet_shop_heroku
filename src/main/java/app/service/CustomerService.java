package app.service;

import app.converters.CustomerConverter;
import app.dto.CustomerDTO;
import app.entities.Customer;
import app.exceptions.TransactionExecutionException;
import app.exceptions.ValidationException;
import app.repositories.CustomerRepository;
import app.repositories.TransactionHandler;

import java.sql.SQLException;
import java.util.List;

public class CustomerService {
    private static final CustomerRepository customerRepository = new CustomerRepository();
    private static final CustomerConverter customerConverter = new CustomerConverter();

    public List<CustomerDTO> getAll() {
        TransactionHandler<List<Customer>> transactionHandler = new TransactionHandler<>(customerRepository::getAll);
        try {
            return customerConverter.toCustomerDTOList(transactionHandler.execute());
        } catch (SQLException e) {
            throw new TransactionExecutionException(e);
        }
    }

    public boolean changeCustomerStatus(String customerId) {
        TransactionHandler<Boolean> transactionHandler = new TransactionHandler<>(connection -> {
            Customer customer = customerRepository.getById(connection, Integer.valueOf(customerId));
            if (customer != null) {
                Character status = "Y".equals(customer.getIsBlocked()) ? 'N' : 'Y';
                return customerRepository.changeStatus(connection, customer.getId(), status);
            } else throw new ValidationException("Cant block customer - already blocked or doesnt exist");
        });
        try {
            return transactionHandler.execute();
        } catch (SQLException e) {
            throw new TransactionExecutionException(e);
        }
    }
}
