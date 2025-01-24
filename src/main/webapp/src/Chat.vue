<script setup lang="ts">
import { shallowRef, watch } from "vue";
import dayjs from "dayjs";

export type Message = {
  username: string
  text: string
  timestamp: number
}

const message = shallowRef<string>(localStorage.getItem("message") ?? "")
watch(message, v => localStorage.setItem("message", v))

defineEmits<{
  send: [message: string]
}>()

defineProps<{
  messages: Message[]
}>()

</script>

<template>
  <div>
    <ul class="messages">
      <li v-for="{username, text, timestamp} in messages">
        <p v-text="`${dayjs.unix(timestamp).format('HH:mm')} - ${username} : ${text}`"/>
      </li>
    </ul>
    <div>
      <input type="text" v-model="message">
      <button @click="$emit('send', message)">send</button>
    </div>
  </div>
</template>

<style scoped>
.messages {
  max-width: 100%;
}
</style>
