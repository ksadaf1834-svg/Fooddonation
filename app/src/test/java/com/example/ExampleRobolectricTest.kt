package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.security.PasswordHasher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app_name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FoodShare", appName)
  }

  @Test
  fun `password hashing and verification test`() {
    val password = "SecurePassword123!"
    val hash = PasswordHasher.hashPassword(password)
    assertNotEquals(password, hash)
    assertTrue(PasswordHasher.verifyPassword(password, hash))
  }
}
