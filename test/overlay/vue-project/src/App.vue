<script setup>
import { onMounted, ref } from 'vue'

const messageToAndroid = ref('')
const messageFromAndroid = ref('')
onMounted(() => {
  window.receiveMessageFromAndroid = (message) => {
    messageFromAndroid.value = message
  }
})
const sendToAndroid = () => {
  window.Android.send(messageToAndroid.value)
}
</script>

<template>
  <div class="main">
    <input v-model="messageToAndroid" />
    <button @click="sendToAndroid">Send Message to android</button>
    <p>{{ messageFromAndroid }}</p>
  </div>
</template>

<style scoped>
.main {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}
</style>
