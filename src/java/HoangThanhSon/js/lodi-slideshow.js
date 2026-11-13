/* Slideshow banner trang chủ - tách từ app.js của LODI (giữ nguyên hiệu ứng) */
(function () {
  'use strict';
  function $(sel, root) { return (root || document).querySelector(sel); }
  function initSlideshow() {
    var box = $('.banner');
    if (!box) return;
    var slides = [].slice.call(box.querySelectorAll('.slide'));
    var dots = [].slice.call(box.querySelectorAll('.dots button'));
    var bar = $('.progress i', box);
    if (slides.length < 2) return;

    var DELAY = 5500;
    var reduce = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    var cur = 0;
    var leaveTimer = null;
    box.style.setProperty('--delay', DELAY + 'ms');

    function restartProgress() {
      box.classList.remove('playing');
      if (reduce) return;
      void box.offsetWidth;          // ép trình duyệt tính lại để animation chạy lại từ đầu
      box.classList.add('playing');
    }

    function show(n, dir) {
      n = (n + slides.length) % slides.length;
      if (n === cur) return;
      dir = dir || 1;                 // 1 = sang phải (next), -1 = quay lại (prev)
      box.style.setProperty('--in', (dir > 0 ? 3 : -3) + '%');
      box.style.setProperty('--out', (dir > 0 ? -3 : 3) + '%');

      slides.forEach(function (s) { s.classList.remove('leaving'); });
      var old = slides[cur];
      old.classList.remove('active');
      old.classList.add('leaving');   // giữ slide cũ bên dưới cho tới khi slide mới hiện rõ
      slides[n].classList.add('active');

      dots[cur] && (dots[cur].classList.remove('on'), dots[cur].removeAttribute('aria-current'));
      dots[n] && (dots[n].classList.add('on'), dots[n].setAttribute('aria-current', 'true'));

      clearTimeout(leaveTimer);
      leaveTimer = setTimeout(function () { old.classList.remove('leaving'); }, 950);
      cur = n;
      restartProgress();
    }
    function next() { show(cur + 1, 1); }
    function prev() { show(cur - 1, -1); }

    /* Điều khiển */
    var bNext = $('.next', box), bPrev = $('.prev', box);
    bNext && bNext.addEventListener('click', next);
    bPrev && bPrev.addEventListener('click', prev);
    dots.forEach(function (d, i) {
      d.addEventListener('click', function () { show(i, i > cur ? 1 : -1); });
    });
    box.addEventListener('keydown', function (e) {
      if (e.key === 'ArrowRight') { next(); }
      else if (e.key === 'ArrowLeft') { prev(); }
    });

    /* Vuốt trái/phải trên màn hình cảm ứng */
    var x0 = null;
    box.addEventListener('touchstart', function (e) { x0 = e.touches[0].clientX; }, { passive: true });
    box.addEventListener('touchend', function (e) {
      if (x0 === null) return;
      var dx = e.changedTouches[0].clientX - x0;
      x0 = null;
      if (Math.abs(dx) > 45) { dx < 0 ? next() : prev(); }
    }, { passive: true });

    /* Tự chạy: hết thanh tiến trình thì sang slide kế tiếp */
    if (bar) bar.addEventListener('animationend', next);
    function pause() { box.classList.add('paused'); }
    function resume() { box.classList.remove('paused'); }
    box.addEventListener('mouseenter', pause);
    box.addEventListener('mouseleave', resume);
    box.addEventListener('focusin', pause);
    box.addEventListener('focusout', resume);
    document.addEventListener('visibilitychange', function () {
      document.hidden ? pause() : resume();
    });

    /* Chạy hiệu ứng vào cho slide đầu tiên khi tải trang */
    slides[0].classList.remove('active');
    void slides[0].offsetWidth;
    slides[0].classList.add('active');
    dots[0] && dots[0].setAttribute('aria-current', 'true');
    restartProgress();
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', initSlideshow); else initSlideshow();
})();
