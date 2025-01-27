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
  <div class="chat-layout">
    <ul class="messages">
      <li v-for="{username, text, timestamp} in messages">
        <p v-text="`${dayjs.unix(timestamp).format('HH:mm')} - ${username} : ${text}`"/>
      </li>
    </ul>

    <div class="input-section">
      <input type="text" v-model="message">
      <button @click="$emit('send', message)">send</button>
    </div>
  </div>
</template>

<style scoped>
.chat-layout {
  display: flex;
  flex-flow: column nowrap;
  justify-content: end;
}

ul {
  padding: 0;
}

ul li {
  list-style-type: none;
  margin: 0;
}

.input-section {
  display: grid;
  grid-template-columns: 1fr 0.4fr;
  gap: 8px;
}
</style>
