package com.jaredmuralt.stockmarkettrainer.util

object StockSymbols {

    fun getRandomSymbol(): String {
        return symbols.random()
    }

    private val symbols = arrayOf(
        "AAPL",
        "MSFT",
        "AMZN",
        "GOOGL",
        "FB",
        "BABA",
        "TSLA",
        "MSFT",
        "PG",
        "WMT",
        "MA",
        "NFLX",
        "BAC",
        "DIS",
        "NVDA",
        "HD",
        "JPM",
        "T",
        "PFE",
        "INTC",
        "CSCO",
        "CMCSA",
        "SAP",
        "CDK",
        "CRM",
        "ABT",
        "ORCL",
        "CVX",
        "COST",
        "NKE",
        "TMUS",
        "SHOP",
        "ABC",
        "UPS",
        "BUD",
        "AMD",
        "BLK",
        "SBUX",
        "SNE",
        "INTU",
        "CAT",
        "FISV",
        "SQ",
        "GOOGL",
        "AEO",
        "ADT",
        "AME",
        "BA",
        "BAC",
        "BB",
        "BDC",
        "BEST",
        "BSX",
        "CBT",
        "CDR",
        "DHI",
        "DK",
        "DS",
        "EAT",
        "EFX",
        "ENZ",
        "FDX",
        "FIT",
        "FMC",
        "GCO",
        "GDDY",
        "GGG",
        "IGA",
        "ITT",
        "JELD",
        "JOE",
        "K",
        "KAI",
        "KEX",
        "LEE",
        "LNN",
        "MAN",
        "MAS",
        "MCD",
        "NHI",
        "NLS",
        "OVV",
        "PAC",
        "PB",
        "PH",
        "QD",
        "R",
        "RENN",
        "SCS",
        "SEE",
        "TEX",
        "TGI",
        "TIF"
    )
}