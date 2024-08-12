package org.henry.onlinebankingsystemp.service;

public interface AccountService {
//    private final AccountRepository accountRepository;
//    private final TransactionRepository transactionRepository;
//    private final UserRepository userRepository;
//    private final AccountNumberGenerator generator;
//
//    private Long getUserId(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return ((Customer) authentication.getPrincipal()).getCustomerId();
//    }
//    public Customer getDetails(Long id) {
//        return userRepository.findById(id).orElseThrow(
//                () -> new IllegalStateException("Customer with id " + id + " does not exist"));
//    }
//    private final Supplier<Customer> getCurrentUser = () -> { Long id = getUserId(); Customer customer = getDetails(id);
//        return customer;
//    };
//
//    private Account getTarget(String number){
//        log.info("Fetching Account Number");
//        return accountRepository.findByAccountNumber(number)
//                .orElseThrow(() -> new IllegalStateException("Account does not exist"));
//    }
//
//    private LocalDateTime MillisToDateTime() {
//            long millis = System.currentTimeMillis();
//            Instant instant = Instant.ofEpochMilli(millis);
//            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
//            return localDateTime;
//    }
//
//    public DefaultApiResponse transferMoney(TransferDTO request) {
//        BalanceDto userBalance = new BalanceDto();
//        Transaction transaction = new Transaction();
//        DefaultApiResponse res = new DefaultApiResponse();
//
//        Customer customer = getCurrentUser.get();
//        Account userAccount = customer.getAccount();
//        String targetAccountNumber = request.getTargetAccountNumber();
//
//        Account targetAccount = getTarget(targetAccountNumber);
//        Customer targetCustomer = getDetails(targetAccount.getCustomerId());
//
//        if(request.getAmount().compareTo(BigDecimal.valueOf(200)) < 0){
//            res.setStatusCode(500);
//            res.setStatusMessage("Can't transfer less than 200 NGN");
//            return res;
//        }
//
//        if(request.getAmount().compareTo(customer.getAccount().getBalance()) > 0){
//            res.setStatusCode(500);
//            res.setStatusMessage("Insufficient Balance");
//            return res;
//        }
//
//        transaction.setCustomer(customer);
//        targetAccount.setBalance(targetCustomer.getAccount().getBalance().add(request.getAmount()));
//        transaction.setAccount(targetAccount);
//        transaction.setTransactionType(TransactionType.TRANSFER);
//        transaction.setTransactionDate(MillisToDateTime());
//        transaction.setTargetAccountNumber(String.valueOf(request.getAmount()));
//        transaction.setAmount(request.getAmount());
//        transaction.setDebit(request.getAmount());
//        transaction.setCredit(null);
//        transaction.setRunningBalance(request.getAmount());
//        transaction.setTransactionRef(generator.generateReference());
//        userAccount.setBalance(customer.getAccount().getBalance().subtract(request.getAmount()));
//
//        userBalance.setUsername(customer.getUsername());
//        userBalance.setBalance(customer.getAccount().getBalance().subtract(request.getAmount()));
//
//        accountRepository.save(userAccount);
//        accountRepository.save(targetAccount);
//        userRepository.save(customer);
//        userRepository.save(targetCustomer);
//        transactionRepository.save(transaction);
//
//        res.setStatusCode(200);
//        res.setStatusMessage("Transfer Successful");
//
//        return res;
//    }
//
//    public BigDecimal getDailyTransactionAmount(Long id) {
//        List<Transaction> transactions = transactionRepository.findTransactionByCustomer(id);
//        BigDecimal totalAmount = BigDecimal.valueOf(0.0);
//        for(Transaction tran : transactions){
//            if(tran.getTransactionType().equals(TransactionType.DEPOSIT)){
//                continue;
//            }
//            totalAmount = totalAmount.add(tran.getAmount());
//        }
//        return totalAmount;
//    }
//
//    public DefaultApiResponse updateBalance(TransactionDTO request, TransactionType transactionType, String operation){
//        DefaultApiResponse res = new DefaultApiResponse();
//        try {
//            BalanceDto userBalance = new BalanceDto();
//            Customer customer = getCurrentUser.get();
//
//            log.info("Comparing Balance and amount returned");
//            if(request.getAmount().compareTo(BigDecimal.ZERO) < 0){
//                res.setStatusCode(500);
//                res.setStatusMessage("Invalid amount");
//                return res;
//            }
//
//            int b1 = request.getAmount().compareTo(customer.getAccount().getBalance());
//            boolean b2 = request.getAmount().compareTo(customer.getAccount().getBalance()) == -1;
//            boolean b3 = request.getAmount().compareTo(customer.getAccount().getBalance()) < 0;
//            boolean b4 = request.getAmount().compareTo(customer.getAccount().getBalance()) > 0;
//            boolean b5 = request.getAmount().compareTo(customer.getAccount().getBalance()) == 1;
//
//            log.info("Checking for adequate balance");
//            if(request.getAmount().compareTo(customer.getAccount().getBalance()) != -1 && transactionType == TransactionType.WITHDRAWAL){
//                res.setStatusCode(500);
//                res.setStatusMessage("Insufficient Balance");
//                return res;
//            }
//
//            log.info("Performing Transaction Limit Check");
//            if(transactionType != TransactionType.DEPOSIT){
//                if(getDailyTransactionAmount(customer.getCustomerId()).add(request.getAmount()).compareTo(customer.getAccount().getTransactionLimit()) > 0){
//                    res.setStatusCode(500);
//                    res.setStatusMessage("You have exceeded your transaction limit for today");
//                    return res;
//                }
//            }
//
//            BigDecimal newBalance;
//            if(operation.equals("addition")){
//                newBalance = customer.getAccount().getBalance().add(request.getAmount());
//            }else
//                newBalance = customer.getAccount().getBalance().subtract(request.getAmount());
//
//            log.info("Updating the Database");
//            Account userAccount = customer.getAccount();
//            userAccount.setBalance(newBalance);
//
//            Transaction transaction = new Transaction();
//            transaction.setCustomer(customer);
//            transaction.setAccount(userAccount);
//            transaction.setTransactionType(transactionType);
//            transaction.setTransactionDate(MillisToDateTime());
//            transaction.setTargetAccountNumber(null);
//            transaction.setAmount(request.getAmount());
//            transaction.setBalanceAfterRunningBalance(newBalance);
//            if(transactionType.equals(TransactionType.DEPOSIT)){
//                transaction.setCredit(request.getAmount());
//            }else {
//                transaction.setDebit(request.getAmount());
//            }
//            transaction.setRunningBalance(request.getAmount());
//            transaction.setTransactionRef(generator.generateReference());
//
//
//            userBalance.setUsername(customer.getUsername());
//            userBalance.setBalance(newBalance);
//
//            accountRepository.save(userAccount);
//            userRepository.save(customer);
//            transactionRepository.save(transaction);
//
//            res.setStatusCode(200);
//            res.setStatusMessage(transactionType == TransactionType.WITHDRAWAL ? "Withdrawal Successful" : "Deposit Successful");
//
//            return res;
//        } catch (Exception e) {
//            res.setStatusCode(500);
//            res.setStatusMessage(e.getMessage());
//            return res;
//        }
//    }
//
//    public DefaultApiResponse depositMoney(TransactionDTO request){
//        return updateBalance(request, TransactionType.DEPOSIT, "addition");
//    }
//
//    public DefaultApiResponse withdrawMoney(TransactionDTO request){
//        return updateBalance(request, TransactionType.WITHDRAWAL, "subtract");
//    }
}


