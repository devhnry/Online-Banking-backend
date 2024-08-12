package org.henry.onlinebankingsystemp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.henry.onlinebankingsystemp.entity.Customer;
import org.henry.onlinebankingsystemp.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

@Service
@Slf4j
@RequiredArgsConstructor
public class VirtualCardService {
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    private static String secretKey = "sk_test_cNNwP1m6g3iDBFDjVDFL7xLFTsgL3WZ2HCsn3diK";
    private static String baseUrl = "https://api.korapay.com/merchant/api/v1/api/v1/";

    private Long getUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((Customer) authentication.getPrincipal()).getCustomerId();
    }
    private Customer getDetails(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("Customer with id " + id + "does not exist"));
    }
    private Supplier<Customer> getCurrentUser = () -> { Long id = getUserId(); Customer customer = getDetails(id);
        return customer;
    };
//
//    private CardHolderDTO createCardHolder(CardHolderDTO cardHolder){
//        try {
//            CardHolderDTO holder = createCardHolderFromCustomer(cardHolder);
//            log.info("Creating cardHolder Account for Customer");
//            new CardHolderDTO();
//            HttpEntity<CardHolderDTO> httpEntity = new HttpEntity<>(holder,getHttpHeader());
//            String url = baseUrl + "cardholders";
//            var response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, CardHolderDTO.class);
//            CardHolderDTO cardholder = response.getBody();
//            return cardholder;
//        } catch (RestClientException e) {
//            log.error(e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }

//    private CardHolderDTO createCardHolderFromCustomer(CardHolderDTO cardHolder) {
//        Customer customer = getCurrentUser.get();
//        var customerAddress = customer.getAddress();
//        var address = new AddressDTO();
//
//        address.setCity(customerAddress.getCity());
//        address.setCountry(customerAddress.getCountry());
//        address.setState(customerAddress.getState());
//        address.setZip_code(customerAddress.getZipCode());
//        address.setStreet(customerAddress.getStreet());
//
//        cardHolder.setFirst_name(customer.getFirstName());
//        cardHolder.setLast_name(customer.getLastName());
//        cardHolder.setEmail(customer.getEmail());
//        cardHolder.setPhone(customer.getPhone());
//        cardHolder.setDate_of_birth(customer.getDateOfBirth());
//        cardHolder.setAddress(address);
//
//        cardHolder.getCountry_identity().setType(customer.getCountry().getType());
//        cardHolder.getCountry_identity().setNumber(customer.getCountry().getNumber());
//
//        cardHolder.getIdentity().setCountry(customer.getIdentity().getCountry());
//        cardHolder.getIdentity().setType(customer.getIdentity().getType());
//        cardHolder.getIdentity().setImage(customer.getIdentity().getImage());
//        cardHolder.getIdentity().setNumber(customer.getIdentity().getNumber());
//
//        return cardHolder;
//    }

    private HttpHeaders getHttpHeader(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer" + secretKey);
        return headers;
    }
}
