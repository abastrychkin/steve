package de.rwth.idsg.steve.service;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import static jooq.steve.db.Tables.TRANSACTION;

@Service
public class CurrentControlService {
    int maxGridCurrentA = 60;

    @Autowired
    private DSLContext ctx;

    private int getNumActiveTransactions() {
        Field<Integer> numTransactions = ctx.selectCount()
                .from(TRANSACTION)
                .where(TRANSACTION.STOP_TIMESTAMP.isNull())
                .asField("num_transactions");
        Record1<Integer> resultRecord = ctx.select(numTransactions).fetchOne();
        return resultRecord.value1();
    }

    @Scheduled(fixedRate = 5000)
    private void runFixedRateTask() {
        System.out.println("Fixed rate task executed at: " + System.currentTimeMillis());

        int currentNumActiveTransactions = getNumActiveTransactions();

        System.out.println("Num of active transactions: " +  currentNumActiveTransactions);

        if (currentNumActiveTransactions > 0) {
            int maxCurrentPerTransactionA = maxGridCurrentA /  currentNumActiveTransactions;
            System.out.println("Max current per transaction: " +  maxCurrentPerTransactionA + "A");
        }
    }
}
