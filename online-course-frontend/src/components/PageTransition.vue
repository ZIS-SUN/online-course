<template>
  <transition
    :name="transition"
    mode="out-in"
    @before-enter="onBeforeEnter"
    @enter="onEnter"
    @after-enter="onAfterEnter"
    @before-leave="onBeforeLeave"
    @leave="onLeave"
    @after-leave="onAfterLeave"
  >
    <slot />
  </transition>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  name: {
    type: String,
    default: 'fade-slide'
  },
  duration: {
    type: Number,
    default: 300
  }
})

const emit = defineEmits(['before-enter', 'enter', 'after-enter', 'before-leave', 'leave', 'after-leave'])

const transition = computed(() => props.name)

const onBeforeEnter = (el) => {
  emit('before-enter', el)
}

const onEnter = (el, done) => {
  emit('enter', el, done)
  setTimeout(done, props.duration)
}

const onAfterEnter = (el) => {
  emit('after-enter', el)
}

const onBeforeLeave = (el) => {
  emit('before-leave', el)
}

const onLeave = (el, done) => {
  emit('leave', el, done)
  setTimeout(done, props.duration)
}

const onAfterLeave = (el) => {
  emit('after-leave', el)
}
</script>

<style scoped>
/* Fade Slide Transition */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

/* Slide Left Transition */
.slide-left-enter-active,
.slide-left-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-left-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

/* Slide Right Transition */
.slide-right-enter-active,
.slide-right-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-right-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.slide-right-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

/* Scale Fade Transition */
.scale-fade-enter-active,
.scale-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.scale-fade-enter-from {
  opacity: 0;
  transform: scale(0.95);
}

.scale-fade-leave-to {
  opacity: 0;
  transform: scale(1.05);
}

/* Zoom Transition */
.zoom-enter-active,
.zoom-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.zoom-enter-from {
  opacity: 0;
  transform: scale(0.8);
}

.zoom-leave-to {
  opacity: 0;
  transform: scale(1.2);
}

/* Bounce Transition */
.bounce-enter-active {
  animation: bounce-in 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.bounce-leave-active {
  animation: bounce-out 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes bounce-in {
  0% {
    opacity: 0;
    transform: scale(0.3);
  }
  50% {
    opacity: 1;
    transform: scale(1.05);
  }
  70% {
    transform: scale(0.9);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes bounce-out {
  0% {
    opacity: 1;
    transform: scale(1);
  }
  100% {
    opacity: 0;
    transform: scale(0.8);
  }
}

/* Flip Transition */
.flip-enter-active,
.flip-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.flip-enter-from {
  opacity: 0;
  transform: rotateY(90deg);
}

.flip-leave-to {
  opacity: 0;
  transform: rotateY(-90deg);
}

/* Elastic Transition */
.elastic-enter-active {
  animation: elastic-in 0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.elastic-leave-active {
  animation: elastic-out 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes elastic-in {
  0% {
    opacity: 0;
    transform: scale(0) rotate(45deg);
  }
  60% {
    opacity: 1;
    transform: scale(1.1) rotate(-10deg);
  }
  80% {
    transform: scale(0.95) rotate(2deg);
  }
  100% {
    opacity: 1;
    transform: scale(1) rotate(0deg);
  }
}

@keyframes elastic-out {
  0% {
    opacity: 1;
    transform: scale(1) rotate(0deg);
  }
  100% {
    opacity: 0;
    transform: scale(0.8) rotate(-5deg);
  }
}
</style>