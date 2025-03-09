<script setup lang="ts">

</script>

<template>
  <div class="button">
    <slot />
  </div>
</template>

<style scoped>
.button {
  --color: var(--vt-c-indigo);
  color: var(--color);
  padding: 0.5rem;
  width: fit-content;
  height: fit-content;
  border: 0.2rem solid oklch(from var(--color) calc(l - 0.45) c h);
  border-radius: 0.5rem;
}

.button:hover {
  box-shadow: 0 0 0.5rem oklch(from var(--color) calc(l + 0.25) c h);
}
</style>
