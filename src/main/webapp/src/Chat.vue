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

defineProps<{
  messages: Message[]
}>()

const emits = defineEmits<{
  send: [message: string]
}>()

function send(msg: string): void {
  if (msg.trim() === "") return
  emits('send', msg)
  message.value = ""
}
</script>

<template>
  <div class="chat-layout">
    <div class="hand">
      <ul class="messages">
        <li v-for="{username, text, timestamp} in messages">
          <p v-text="`${dayjs(timestamp).format('HH:mm')} - ${username} : ${text}`"/>
        </li>
      </ul>
    </div>

    <div class="input-section">
      <input
        class="bordered rounded clickable"
        type="text"
        @keyup.enter="send(message)"
        v-model="message"
      />
      <div
        class="clickable rounded bordered padded row center-content"
        @click="send(message)"
        :disabled="message.trim()===''"
      >
        <span style="width: min-content">send</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.chat-layout {
  display: flex;
  flex-flow: column nowrap;
  justify-content: end;
}

.hand {
  overflow-y: auto;
  display: flex;
  flex-direction: column-reverse;
}

ul {
  padding: 0;
}

ul li {
  list-style-type: none;
  overflow-wrap: break-word;
  margin: 0;
}

.input-section {
  display: grid;
  grid-template-columns: 1fr 0.4fr;
  gap: 8px;
}

.input-section input {
  width: 100%;
}
</style>
