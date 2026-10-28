import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatbotService } from '../../services/chatbot.service';
import { ChatMessage } from '../../models/chatbot.model';

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="chat-container">
      <div class="chat-header">
        <div class="bot-badge">🤖</div>
        <div>
          <h2>Assistant Conversationnel IA</h2>
          <p class="subtitle">Interrogez les marchés, les fournisseurs et les livraisons en langage naturel</p>
        </div>
      </div>

      <!-- Zone de messages -->
      <div class="chat-messages" #scrollBox>
        <div *ngFor="let m of messages" class="message-wrapper" [ngClass]="m.emetteur">
          <div class="message-avatar">{{ m.emetteur === 'bot' ? '🤖' : '👤' }}</div>
          <div class="message-bubble">
            <div class="message-text" [innerHTML]="formaterTexte(m.texte)"></div>
            <div class="message-time">{{ m.date | date:'HH:mm' }}</div>
          </div>
        </div>

        <div *ngIf="loading" class="message-wrapper bot">
          <div class="message-avatar">🤖</div>
          <div class="message-bubble typing">
            <span class="dot"></span><span class="dot"></span><span class="dot"></span>
          </div>
        </div>
      </div>

      <!-- Suggestions rapides cliquables -->
      <div class="suggestions-bar" *ngIf="suggestions.length > 0">
        <span class="suggestions-label">Suggestions :</span>
        <button *ngFor="let s of suggestions" (click)="poserQuestion(s)" class="btn-suggestion">
          {{ s }}
        </button>
      </div>

      <!-- Champ de saisie -->
      <div class="chat-input-area">
        <input
          type="text"
          [(ngModel)]="questionSaisie"
          (keyup.enter)="envoyer()"
          placeholder="Posez votre question en français... (Ex: Quels marchés sont en cours ?)"
          [disabled]="loading"
        />
        <button (click)="envoyer()" [disabled]="!questionSaisie.trim() || loading" class="btn-send">
          <span>Envoyer</span>
          <span>➤</span>
        </button>
      </div>
    </div>
  `,
  styles: [`
    .chat-container {
      background: #ffffff;
      border: 1px solid #e2e8f0;
      border-radius: 14px;
      height: calc(100vh - 120px);
      display: flex;
      flex-direction: column;
      overflow: hidden;
    }
    .chat-header {
      padding: 16px 20px;
      border-bottom: 1px solid #e2e8f0;
      display: flex;
      align-items: center;
      gap: 14px;
      background: #f8fafc;
    }
    .bot-badge {
      font-size: 26px;
      background: #eff6ff;
      border: 1px solid #bfdbfe;
      width: 44px;
      height: 44px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .chat-header h2 { margin: 0; font-size: 17px; color: #0f172a; }
    .subtitle { margin: 2px 0 0 0; font-size: 12px; color: #64748b; }
    .chat-messages {
      flex: 1;
      padding: 20px;
      overflow-y: auto;
      display: flex;
      flex-direction: column;
      gap: 16px;
      background: #ffffff;
    }
    .message-wrapper {
      display: flex;
      gap: 12px;
      max-width: 80%;
    }
    .message-wrapper.bot { align-self: flex-start; }
    .message-wrapper.utilisateur {
      align-self: flex-end;
      flex-direction: row-reverse;
    }
    .message-avatar {
      width: 34px;
      height: 34px;
      border-radius: 50%;
      background: #f1f5f9;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 16px;
    }
    .message-bubble {
      padding: 12px 16px;
      border-radius: 12px;
      font-size: 13px;
      line-height: 1.5;
    }
    .bot .message-bubble {
      background: #f1f5f9;
      color: #0f172a;
      border-bottom-left-radius: 2px;
    }
    .utilisateur .message-bubble {
      background: #2563eb;
      color: #ffffff;
      border-bottom-right-radius: 2px;
    }
    .message-time {
      font-size: 10px;
      margin-top: 6px;
      opacity: 0.7;
      text-align: right;
    }
    .typing { display: flex; align-items: center; gap: 4px; padding: 14px 18px; }
    .dot {
      width: 6px; height: 6px; background: #64748b; border-radius: 50%;
      animation: blink 1.2s infinite alternate;
    }
    .dot:nth-child(2) { animation-delay: 0.2s; }
    .dot:nth-child(3) { animation-delay: 0.4s; }
    @keyframes blink { from { opacity: 0.2; } to { opacity: 1; } }
    .suggestions-bar {
      padding: 10px 20px;
      background: #f8fafc;
      border-top: 1px solid #e2e8f0;
      display: flex;
      align-items: center;
      gap: 8px;
      overflow-x: auto;
      white-space: nowrap;
    }
    .suggestions-label { font-size: 11px; font-weight: 700; color: #64748b; }
    .btn-suggestion {
      background: #ffffff;
      border: 1px solid #cbd5e1;
      padding: 5px 12px;
      border-radius: 16px;
      font-size: 11px;
      font-weight: 600;
      color: #1e293b;
      cursor: pointer;
      transition: all 0.2s;
    }
    .btn-suggestion:hover { background: #eff6ff; border-color: #93c5fd; color: #1e40af; }
    .chat-input-area {
      padding: 16px 20px;
      border-top: 1px solid #e2e8f0;
      display: flex;
      gap: 12px;
      background: #ffffff;
    }
    .chat-input-area input {
      flex: 1;
      padding: 12px 16px;
      font-size: 14px;
      border: 1px solid #cbd5e1;
      border-radius: 8px;
      outline: none;
    }
    .chat-input-area input:focus { border-color: #2563eb; }
    .btn-send {
      background: #2563eb;
      color: #fff;
      border: none;
      padding: 12px 20px;
      border-radius: 8px;
      font-size: 14px;
      font-weight: 700;
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 6px;
    }
    .btn-send:disabled { opacity: 0.5; cursor: not-allowed; }
  `]
})
export class ChatbotComponent implements OnInit {
  messages: ChatMessage[] = [];
  questionSaisie = '';
  loading = false;
  suggestions: string[] = [];

  constructor(private chatbotService: ChatbotService) {}

  ngOnInit(): void {
    // Message d'accueil initial
    this.messages.push({
      id: '1',
      emetteur: 'bot',
      texte: 'Bonjour ! Je suis l\'assistant conversationnel de gestion des marchés de l\'**École Polytechnique de Thiès** (EPT).\n\nPosez-moi vos questions sur les marchés, les livraisons, les besoins ou les prestataires.',
      date: new Date()
    });

    this.chatbotService.getSuggestions().subscribe({
      next: (res) => {
        if (res.succes && res.donnees) {
          this.suggestions = res.donnees;
        }
      }
    });
  }

  poserQuestion(question: string): void {
    this.questionSaisie = question;
    this.envoyer();
  }

  envoyer(): void {
    const q = this.questionSaisie.trim();
    if (!q || this.loading) return;

    this.messages.push({
      id: Date.now().toString(),
      emetteur: 'utilisateur',
      texte: q,
      date: new Date()
    });

    this.questionSaisie = '';
    this.loading = true;

    this.chatbotService.envoyerMessage(q).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.succes && res.donnees) {
          this.messages.push({
            id: Date.now().toString(),
            emetteur: 'bot',
            texte: res.donnees.reponse,
            date: new Date(),
            type: res.donnees.type as any,
            donnees: res.donnees.donnees
          });

          if (res.donnees.suggestions && res.donnees.suggestions.length > 0) {
            this.suggestions = res.donnees.suggestions;
          }
        }
      },
      error: () => {
        this.loading = false;
        this.messages.push({
          id: Date.now().toString(),
          emetteur: 'bot',
          texte: 'Désolé, une erreur de communication est survenue avec le service de recherche.',
          date: new Date()
        });
      }
    });
  }

  formaterTexte(texte: string): string {
    if (!texte) return '';
    // Format simple pour gras et retours à la ligne
    return texte
      .replace(/\n/g, '<br/>')
      .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
      .replace(/\*(.*?)\*/g, '<em>$1</em>')
      .replace(/`(.*?)`/g, '<code style="background:#e2e8f0;padding:2px 4px;border-radius:3px;">$1</code>');
  }
}
