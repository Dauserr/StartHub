package com.example.starthub.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter

@Composable
fun ProfileScreen() {
    var name by remember { mutableStateOf("John") }
    var surname by remember { mutableStateOf("Doe") }
    var role by remember { mutableStateOf("Software Developer") }
    var photoUrl by remember { mutableStateOf<String?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Profile Photo
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
        ) {
            if (photoUrl != null) {
                // If there's a photo URL, display it
                Image(
                    painter = rememberAsyncImagePainter(photoUrl),
                    contentDescription = "Profile Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder if no photo
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Profile Photo",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Name
        Text(
            text = "$name $surname",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Role
        Text(
            text = role,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Profile Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                ProfileInfoRow(label = "Name", value = name)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                ProfileInfoRow(label = "Surname", value = surname)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                ProfileInfoRow(label = "Role", value = role)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Edit Profile Button
        Button(
            onClick = { showEditDialog = true },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit Profile")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logout Button
        OutlinedButton(
            onClick = { /* Handle logout */ },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Logout")
        }
    }

    // Edit Profile Dialog
    if (showEditDialog) {
        EditProfileDialog(
            currentName = name,
            currentSurname = surname,
            currentRole = role,
            onDismiss = { showEditDialog = false },
            onSave = { newName, newSurname, newRole ->
                name = newName
                surname = newSurname
                role = newRole
                showEditDialog = false
            }
        )
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentSurname: String,
    currentRole: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var surname by remember { mutableStateOf(currentSurname) }
    var role by remember { mutableStateOf(currentRole) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text("Surname") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Role") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, surname, role) },
                enabled = name.isNotBlank() && surname.isNotBlank() && role.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}