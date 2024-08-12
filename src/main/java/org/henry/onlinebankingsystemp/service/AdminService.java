package org.henry.onlinebankingsystemp.service;

public interface AdminService {
//    private final UserRepository userRepository;
//    private final AuthenticationService authenticationService;
//
//    public DefaultApiResponse suspendUser(Long id){
//        DefaultApiResponse res = new DefaultApiResponse();
//        Customer customer = userRepository.findById(id).orElseThrow(
//                () -> {
//                    log.error("Customer does not exist");
//                    return new UsernameNotFoundException(String.format("Customer with id %l does not exist", id));
//                }
//        );
//
//        customer.setIsSuspended(true);
//        authenticationService.revokeAllUserTokens(customer);
//        res.setStatusCode(200);
//        res.setStatusMessage("Successfully suspended users");
//        log.info("Customer has been saved successfully");
//        userRepository.save(customer);
//        return res;
//    }
}
