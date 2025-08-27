package org.app.inflo.screens.home.creator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.inflo.core.data.models.CampaignAdditionalQuestionAppModel
import org.app.inflo.core.theme.AppTheme
import org.app.inflo.core.ui.AppPrimaryButton
import org.app.inflo.core.ui.AppTextField

@Composable
fun ExtraQuestionsBottomSheet(
    questions: List<CampaignAdditionalQuestionAppModel>,
    onQuestionAnswered: (CampaignAdditionalQuestionAppModel, String) -> Unit,
    enableContinueBtn: Boolean,
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Information needed for this partnership.",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontSize = 24.sp,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Questions and input fields
        questions.forEachIndexed { index, question ->
            QuestionInputSection(
                question = question,
                onAnswerEntered = { answer ->
                    onQuestionAnswered.invoke(question, answer)
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            if (index < questions.size - 1) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Continue Button
        AppPrimaryButton(
            text = "Continue",
            onClick = onContinueClicked,
            modifier = Modifier.fillMaxWidth(),
            enabled = enableContinueBtn
        )
    }
}

@Composable
private fun QuestionInputSection(
    question: CampaignAdditionalQuestionAppModel,
    onAnswerEntered: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        // Question label
        Text(
            text = question.question,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Input field using AppTextField
        AppTextField(
            value = question.answer,
            onValueChange = onAnswerEntered,
            placeholder = { Text("Enter your answer") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
} 