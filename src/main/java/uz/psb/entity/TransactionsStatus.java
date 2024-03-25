package uz.psb.entity;

public enum TransactionsStatus {
    //    transaksiya butunlay tugatilsa
    FINISHED,
    //    bir tomondan yechib boshqa accountdan yechmay tursa
    PROCESS,
    //    transaksiya muvaffaqiyatsiz bo'lsa yoziladi
    CANCEL
}
