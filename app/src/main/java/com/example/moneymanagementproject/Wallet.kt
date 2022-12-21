package com.example.moneymanagementproject

class Wallet (
    var nameWallet: String,
    var saldo: Int = 0
        ){

    fun increaseWallet(amount : Int) {
        this.saldo += amount
    }

    fun decreaseWallet(amount : Int) {
        if(this.saldo - amount > 0){
            this.saldo -= amount
        }
    }

    fun getWalletName(): String {
        return this.nameWallet
    }

}