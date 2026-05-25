<template>
  <div class="history-page">
    <section class="history-hero">
      <div class="history-hero-overlay"></div>

      <div class="history-hero-content">
        <span class="eyebrow">Desde 1982</span>
        <h1>Tradição que se amassa à mão</h1>
        <p>"O segredo não está na receita, mas no tempo que dedicamos a cada pão."</p>
      </div>
    </section>

    <section class="story-section">
      <div class="story-text">
        <h2>O Legado de um Sonho Familiar</h2>
        <p>
          Como toda empresa familiar, a Padaria União nasceu de um sonho. E assim como todo sonho, ela possui uma bela história. Hoje, é
          gratificante saber o quanto o nome desta padaria é reconhecido pela população de Aracaju.
        </p>
        <p>
          “Seu” Joel trazia dentro de si uma história de empreendedorismo, já tendo vivido uma próspera experiência no ramo de panificação,
          transformando o segmento no Estado.
        </p>
      </div>

      <div class="story-image">
        <img :src="familyImage" alt="Fundadores da Padaria União" />
        <div class="image-caption">Sr. Joel e Dona Jó</div>
      </div>
    </section>

    <section class="story-section reverse">
      <div class="story-image">
        <img :src="oldBakeryImage" alt="Fachada antiga da Padaria União" />
      </div>

      <div class="story-text">
        <h2>Fundação em 1982</h2>
        <p>
          Fundada em 12/07/1982, a Panificadora União nasceu no Conjunto Jessé Pinto Freire. O casal Joel e Jovelina colocou em prática a
          ideia de servir a comunidade com qualidade, compromisso e carinho.
        </p>
        <p>Desde os primeiros pães saídos do forno, a missão sempre foi clara: levar o sabor da tradição para a mesa dos sergipanos.</p>
      </div>
    </section>

    <section class="story-section">
      <div class="story-text">
        <h2>Expansão e Modernização</h2>
        <p>
          Consolidada na comunidade, a Padaria União buscou inovar, abrindo um leque variado de produtos. Foi nessa fase que se fortaleceu a
          fama de possuir produtos artesanais de qualidade.
        </p>
        <p>
          Com reformas e modernizações, a padaria passou a oferecer um ambiente mais acolhedor, mantendo o sabor e os valores que sempre
          fizeram parte da sua história.
        </p>
      </div>

      <div class="story-image">
        <img :src="modernBakeryImage" alt="Padaria União modernizada" />
      </div>
    </section>

    <section class="story-section reverse">
      <div class="story-image">
        <img :src="modernBakeryImage" alt="Interior moderno da Padaria União" />
      </div>

      <div class="story-text">
        <h2>Uma Nova Roupagem, os Mesmos Valores</h2>
        <p>
          Em 2017, a União buscou inovar sem perder de vista o aceno às tradições. Era preciso trazer uma nova roupagem que não deixasse
          esquecidos os valores que fundaram a padaria há quase quatro décadas.
        </p>
        <p>
          Dando lugar à nova marca "Padaria União", o espaço foi revitalizado para valorizar a arte milenar da panificação em um ambiente
          acolhedor e moderno.
        </p>
      </div>
    </section>

    <section class="story-section">
      <div class="story-text">
        <h2>Um Novo Marco em 2020</h2>
        <p>
          Em 2020, com a chegada do jovem empresário Carlos Victor Paixão, neto de Seu Joel, um novo fôlego fora dado ao projeto da família.
          A União expandiu seu alcance através da produção em larga escala.
        </p>

        <blockquote>
          "Preservamos o sabor artesanal que nos trouxe até aqui, enquanto abraçamos o futuro com novas tecnologias e a mesma paixão de
          1982."
        </blockquote>

        <strong class="quote-author">— Carlos Victor Paixão</strong>
      </div>

      <div class="collage-image">
        <div class="collage-bg"></div>

        <img class="collage-main" :src="modernBakeryImage" alt="Padaria União em 2020" />
        <img class="collage-secondary" :src="newPhaseImage" alt="Nova fase da Padaria União" />
      </div>
    </section>

    <section class="timeline-section">
      <div class="section-heading">
        <h2>Nossa Jornada no Tempo</h2>
        <div class="accent-line"></div>
      </div>

      <div class="timeline">
        <article class="timeline-item active">
          <span>1982</span>
          <h3>O Início</h3>
          <p>Seu Joel e Dona Jó dão início à história da Panificadora União.</p>
        </article>

        <article class="timeline-item">
          <span>2006</span>
          <h3>Expansão</h3>
          <p>Modernização da estrutura e ampliação da variedade de produtos.</p>
        </article>

        <article class="timeline-item">
          <span>2017</span>
          <h3>Nova Fase</h3>
          <p>Nova roupagem visual, sem perder a tradição artesanal.</p>
        </article>

        <article class="timeline-item highlight">
          <span>2020+</span>
          <h3>Futuro</h3>
          <p>Terceira geração na gestão e expansão da marca.</p>
        </article>
      </div>
    </section>

    <section class="history-cta">
      <div>
        <h2>Venha fazer parte desta história</h2>
        <p>Visite nossa casa e sinta o sabor de quatro décadas de dedicação familiar.</p>
      </div>

      <div class="cta-actions">
        <router-link to="/site#localizacao" class="cta-primary">Onde Estamos</router-link>
        <a href="https://wa.me/557932322876" target="_blank" class="cta-secondary">Fale Conosco</a>
      </div>
    </section>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'PublicHistory',
  setup() {
    const heroImage = new URL('../../../content/images/padaria-interna.jpg', import.meta.url).href;
    const familyImage = new URL('../../../content/images/padaria-interna.jpg', import.meta.url).href;
    const oldBakeryImage = new URL('../../../content/images/padaria-interna.jpg', import.meta.url).href;
    const modernBakeryImage = new URL('../../../content/images/padaria-interna.jpg', import.meta.url).href;
    const newPhaseImage = new URL('../../../content/images/padaria-interna.jpg', import.meta.url).href;

    return {
      heroImage,
      familyImage,
      oldBakeryImage,
      modernBakeryImage,
      newPhaseImage,
    };
  },
});
</script>

<style scoped>
.history-page {
  background: #fdfcfb;
  color: #191c20;
}

.history-hero {
  position: relative;
  min-height: 62vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 140px 24px 90px;
  background-image: url('../../../content/images/padaria-interna.jpg');
  background-size: cover;
  background-position: center;
  overflow: hidden;
}

.history-hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(rgba(0, 69, 123, 0.72), rgba(0, 69, 123, 0.52));
}

.history-hero-content {
  position: relative;
  z-index: 2;
  max-width: 880px;
  text-align: center;
  color: white;
}

.eyebrow {
  color: #cc8b29;
  font-weight: 800;
  letter-spacing: 0.3em;
  text-transform: uppercase;
  font-size: 0.78rem;
}

.history-hero-content h1 {
  margin: 20px 0;
  font-family: Georgia, 'Times New Roman', serif;
  font-size: clamp(3rem, 5.8vw, 5.7rem);
  line-height: 0.95;
  font-weight: 800;
  text-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
}

.history-hero-content p {
  max-width: 720px;
  margin: 0 auto;
  font-size: 1.25rem;
  font-style: italic;
  color: rgba(255, 255, 255, 0.92);
}

.story-section {
  max-width: 1180px;
  margin: 0 auto;
  padding: 110px 24px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 80px;
  align-items: center;
}

.story-section + .story-section {
  border-top: 1px solid rgba(0, 69, 123, 0.08);
}

.story-text h2 {
  font-family: Georgia, 'Times New Roman', serif;
  color: #00457b;
  font-size: clamp(2.2rem, 4vw, 4rem);
  line-height: 1.05;
  margin-bottom: 28px;
}

.story-text p {
  color: #64748b;
  font-size: 1.08rem;
  line-height: 1.75;
  margin-bottom: 18px;
}

.story-text blockquote {
  border-left: 4px solid #cc8b29;
  padding-left: 22px;
  margin: 28px 0 18px;
  color: #334155;
  font-size: 1.1rem;
  font-style: italic;
  line-height: 1.7;
}

.quote-author {
  display: block;
  color: #475569;
  margin-top: 16px;
}

.story-image {
  position: relative;
}

.story-image::before {
  content: '';
  position: absolute;
  inset: -16px;
  background: rgba(0, 69, 123, 0.1);
  border-radius: 24px;
  transform: rotate(-3deg);
}

.story-section.reverse .story-image::before {
  background: rgba(204, 139, 41, 0.12);
  transform: rotate(3deg);
}

.story-image img {
  position: relative;
  z-index: 2;
  width: 100%;
  aspect-ratio: 4 / 3;
  object-fit: cover;
  border-radius: 22px;
  box-shadow: 0 28px 60px rgba(15, 23, 42, 0.16);
}

.image-caption {
  position: absolute;
  right: -18px;
  bottom: -18px;
  z-index: 3;
  background: white;
  color: #00457b;
  padding: 14px 18px;
  border-radius: 12px;
  font-family: Georgia, serif;
  font-weight: 700;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.14);
}

.collage-image {
  position: relative;
  min-height: 430px;
}

.collage-bg {
  position: absolute;
  inset: 20px 0 0 30px;
  background: #e7ecf3;
  border-radius: 18px;
  transform: rotate(-2deg);
}

.collage-main {
  position: absolute;
  left: 55px;
  top: 55px;
  width: 48%;
  height: 280px;
  object-fit: cover;
  border-radius: 14px;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.18);
  z-index: 2;
}

.collage-secondary {
  position: absolute;
  right: 20px;
  top: 85px;
  width: 45%;
  height: 260px;
  object-fit: cover;
  border-radius: 14px;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.14);
  z-index: 1;
  opacity: 0.9;
}

.timeline-section {
  background: rgba(0, 69, 123, 0.06);
  padding: 100px 24px;
}

.section-heading {
  text-align: center;
  margin-bottom: 70px;
}

.section-heading h2 {
  font-family: Georgia, serif;
  color: #00457b;
  font-size: 2.7rem;
  font-weight: 800;
}

.accent-line {
  width: 90px;
  height: 4px;
  background: #cc8b29;
  border-radius: 99px;
  margin: 18px auto 0;
}

.timeline {
  max-width: 1180px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 34px;
  position: relative;
}

.timeline::before {
  content: '';
  position: absolute;
  top: 14px;
  left: 0;
  right: 0;
  height: 2px;
  background: rgba(0, 69, 123, 0.2);
}

.timeline-item {
  position: relative;
  padding-top: 44px;
}

.timeline-item::before {
  content: '';
  position: absolute;
  top: 4px;
  left: 0;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: rgba(0, 69, 123, 0.55);
  border: 5px solid #f3f6fb;
  z-index: 2;
}

.timeline-item.active::before,
.timeline-item.highlight::before {
  background: #cc8b29;
}

.timeline-item span {
  color: #cc8b29;
  font-family: Georgia, serif;
  font-size: 1.8rem;
  font-weight: 800;
}

.timeline-item h3 {
  font-family: Georgia, serif;
  margin: 12px 0;
  color: #191c20;
}

.timeline-item p {
  color: #64748b;
  line-height: 1.6;
}

.history-cta {
  max-width: 1180px;
  margin: 90px auto;
  padding: 54px;
  background: linear-gradient(135deg, #00457b, #1d5d9b);
  border-radius: 24px;
  color: white;
  display: flex;
  justify-content: space-between;
  gap: 32px;
  align-items: center;
  box-shadow: 0 28px 70px rgba(0, 69, 123, 0.22);
}

.history-cta h2 {
  font-family: Georgia, serif;
  font-size: 2.6rem;
  margin-bottom: 14px;
}

.history-cta p {
  color: #dbeafe;
  margin: 0;
}

.cta-actions {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
}

.cta-primary,
.cta-secondary {
  padding: 14px 26px;
  border-radius: 10px;
  font-weight: 800;
  text-decoration: none;
}

.cta-primary {
  background: #cc8b29;
  color: white;
}

.cta-secondary {
  background: rgba(255, 255, 255, 0.13);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

@media (max-width: 980px) {
  .story-section,
  .timeline {
    grid-template-columns: 1fr;
  }

  .history-cta {
    flex-direction: column;
    align-items: flex-start;
  }

  .timeline::before {
    display: none;
  }

  .collage-image {
    min-height: 360px;
  }

  .collage-main {
    left: 20px;
    width: 55%;
  }

  .collage-secondary {
    right: 10px;
    width: 48%;
  }
}
</style>
