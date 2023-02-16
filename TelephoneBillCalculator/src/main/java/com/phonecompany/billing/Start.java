package com.phonecompany.billing;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;

public class Start implements TelephoneBillCalculator {

    public static void main(String[] args) {
        String phoneLog = "420774577453,13-01-2020 18:10:15,13-01-2020 18:12:57\n" +
                "320776562352,18-01-2020 08:59:20,18-01-2020 09:10:00\n" +
                "320776562352,18-01-2020 08:59:20,18-01-2020 09:10:00\n"+
                "320776562352,18-01-2020 08:59:20,18-01-2020 09:10:00\n" +
                "320776562352,18-01-2020 08:59:20,18-01-2020 09:10:00\n" + "420776562353,18-01-2020 08:59:20,18-01-2020 09:10:00\n" +
                "420776562353,18-01-2020 08:59:20,18-01-2020 19:11:00\n";
        ;
        Start s = new Start();
        BigDecimal totalBill = s.calculate(phoneLog);
        System.out.println("Částka k uhrazení je:" + totalBill + "Kč");
    }

    @Override
    public BigDecimal calculate(String phoneLog) {
        double normalTax = 0.50;
        double cheapTax = 0.20;
        double increasedTax = 1.00;
        double callRecord;
        double totalAmount = 0.00;
        double totalDiscount = 0.00;

        String[] lines = phoneLog.split("\n");

        HashMap<String, Double> callRecords = new HashMap<>();
        HashMap<String, Integer> phoneNumbers = new HashMap<>();

        Arrays.stream(lines).map(s -> s.split(",")).forEach(line -> {

            String phone = line[0];
            String startTime = line[1];
            String endTime = line[2];

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            try {
                Date d1 = sdf.parse(startTime);
                Date d2 = sdf.parse(endTime);
                long diff = d2.getTime() - d1.getTime();
                long diffSeconds = (diff / (1000 * 60)) % 60;
                double minutes = Math.ceil(diffSeconds);
                int mostFreqNumber = 1;

                if (d1.getHours() >= 8 && d1.getHours() <= 16) {
                    if (minutes > 5) {
                        minutes = 5 * increasedTax + ((minutes - 5) * cheapTax);
                    } else {
                        minutes = minutes * increasedTax;
                    }
                } else {
                    if (minutes > 5) {
                        minutes = 5 * normalTax + ((minutes - 5) * cheapTax);
                    } else {
                        minutes = minutes * increasedTax;
                    }
                }

                if (callRecords.containsKey(phone)) {
                    double existingTime = callRecords.get(phone);
                    callRecords.put(phone, existingTime + minutes);
                    mostFreqNumber = phoneNumbers.get(phone) + 1;
                    phoneNumbers.put(phone, mostFreqNumber);
                } else {
                    callRecords.put(phone, minutes);
                    phoneNumbers.put(phone, mostFreqNumber);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        
        for (String phone : callRecords.keySet()) {
            callRecord = callRecords.get(phone);
            totalAmount = totalAmount + callRecord;
            System.out.println("Key" + phone + "Value" + callRecords.get(phone));
        }

        for (String phone : phoneNumbers.keySet()) {
            double luckyNumber = phoneNumbers.get(phone);
            if (luckyNumber >= 2) {
               totalDiscount = callRecords.get(phone);
            }
            System.out.println("Key" + phone + "Value" + phoneNumbers.get(phone));
        }

        return BigDecimal.valueOf(totalAmount - totalDiscount);

}}