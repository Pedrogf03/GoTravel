package com.gotravel.clienteMovil.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.gotravel.clienteMovil.R

val IBMPlexSans = FontFamily(
        Font(R.font.ibmplexsans_regular, FontWeight.Normal),
        Font(R.font.ibmplexsans_bold, FontWeight.Bold)
)

val Jost = FontFamily(
        Font(R.font.jost_regular, FontWeight.Normal),
        Font(R.font.jost_bold, FontWeight.Bold)
)

// Set of Material typography styles to start with
val Typography = Typography(
        bodyLarge = TextStyle(
                fontFamily = Jost,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp
        ),
        bodySmall = TextStyle(
                fontFamily = Jost,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp
        ),
        labelMedium = TextStyle(
                fontFamily = Jost,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
                fontFamily = Jost,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp
        ),
        titleLarge = TextStyle(
                fontFamily = IBMPlexSans,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
                fontFamily = IBMPlexSans,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.sp
        )
)