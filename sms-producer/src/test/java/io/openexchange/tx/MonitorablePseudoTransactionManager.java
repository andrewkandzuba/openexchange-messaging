package io.openexchange.tx;

import org.springframework.integration.transaction.PseudoTransactionManager;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionUsageException;
import org.springframework.transaction.support.DefaultTransactionStatus;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class MonitorablePseudoTransactionManager extends PseudoTransactionManager {
    private final Map<UUID, PseudoTransaction> transactionMap = new ConcurrentHashMap<>();

    public Stream<PseudoTransaction> transactions() {
        return transactionMap.values().stream();
    }

    @Override
    protected Object doGetTransaction() throws TransactionException {
        PseudoTransaction tx = new PseudoTransaction();
        transactionMap.putIfAbsent(tx.getUuid(), tx);
        return tx;
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
        if (!(transaction instanceof PseudoTransaction))
            throw new UnsupportedTransactionTypeException("Transaction type is not supported: " + transaction.getClass().getCanonicalName());

        PseudoTransaction tx = (PseudoTransaction) transaction;

        if (!transactionMap.containsKey(tx.getUuid()))
            throw new NoTransactionException("Transaction " + tx.getUuid() + " has been not found");

        if (tx.getState() != PseudoTransaction.State.NEW)
            throw new TransactionUsageException("Cannot begin transaction " + tx.getUuid() + " not in NEW state. Current state: " + tx.getState());

        tx.setState(PseudoTransaction.State.BEGIN);
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
        PseudoTransaction tx = verifyPseudoTransaction(status);
        tx.setState(PseudoTransaction.State.COMMIT);
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        PseudoTransaction tx = verifyPseudoTransaction(status);
        tx.setState(PseudoTransaction.State.ROLLBACK);
    }

    private PseudoTransaction verifyPseudoTransaction(DefaultTransactionStatus status) {
        if (!(status.getTransaction() instanceof PseudoTransaction))
            throw new UnsupportedTransactionTypeException("Transaction type is not supported: " + status.getTransaction().getClass().getCanonicalName());

        PseudoTransaction tx = (PseudoTransaction) status.getTransaction();

        if (!transactionMap.containsKey(tx.getUuid()))
            throw new NoTransactionException("Transaction " + tx.getUuid() + " has been not found");

        if (tx.getState() != PseudoTransaction.State.BEGIN)
            throw new TransactionUsageException("Cannot begin transaction " + tx.getUuid() + " not in NEW state. Current state: " + tx.getState());
        return tx;
    }
}
